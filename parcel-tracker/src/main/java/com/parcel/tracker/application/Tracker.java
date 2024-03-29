package com.parcel.tracker.application;

import com.parcel.tracker.application.carrier.CarrierClient;
import com.parcel.tracker.application.carrier.CarrierClientException;
import com.parcel.tracker.domain.Carrier;
import com.parcel.tracker.domain.DomainException;
import com.parcel.tracker.domain.Parcel;
import com.parcel.tracker.domain.ParcelStatus;
import com.parcel.tracker.domain.events.ParcelStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkState;

@Slf4j
@Service
@RequiredArgsConstructor
public class Tracker {
    private static final int PACKAGE_STATUS_CHECK_INTERVAL_MS = 1000 * 60;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private final List<CarrierClient> carrierClients;
    private final ParcelRepository parcelRepository;
    private final List<EventNotifier<ParcelStatusChangedEvent>> notifiers;

    public void startTracking(Parcel parcel) throws ParcelAlreadyTrackedException {
        boolean parcelExists = parcelRepository.existsById(parcel.getId());
        if (parcelExists) {
            throw new ParcelAlreadyTrackedException(parcel);
        }

        processSingleParcel(parcel);
    }

    @Scheduled(fixedDelay = PACKAGE_STATUS_CHECK_INTERVAL_MS)
    public void checkAllTrackers() {
        parcelRepository.findAll().forEach(parcel ->
            executorService.execute(() -> processSingleParcel(parcel))
        );
    }

    private void processSingleParcel(Parcel parcel) {
        log.debug("Processing parcel {}", parcel.getId());

        CarrierClient client = findCarrierClient(parcel.getCarrier());

        ParcelStatus newStatus;
        try {
            newStatus = client.checkParcelStatus(parcel);
        } catch (CarrierClientException e) {
            log.error("Error during parcel status check: {}", e.getMessage());
            return;
        }
        Optional<ParcelStatus> latestStatus = parcel.latestStatus();

        if (latestStatus.isEmpty() || !newStatus.equals(latestStatus.get())) {
            log.info("Received new status {} for parcel {}", newStatus, parcel.getId());

            parcel.addNewStatus(newStatus);
            parcelRepository.save(parcel);
            notifiers.forEach(notifier ->
                    notifier.notify(new ParcelStatusChangedEvent(parcel.getId(), parcel.getCarrier(), newStatus))
            );
        } else {
            log.info("Parcel {} status hasn't changed. Still is {}.", parcel.getId(), latestStatus);
        }
    }

    private CarrierClient findCarrierClient(Carrier carrier) {
        var matchingClients =  carrierClients.stream()
            .filter(client -> client.getCarrier().equals(carrier))
            .toList();

        checkState(
            matchingClients.size() == 1,
            String.format("Found %s carrier clients for name %s. Expected 1.", matchingClients.size(), carrier)
        );

        return matchingClients.get(0);
    }

    public static class ParcelAlreadyTrackedException extends DomainException {
        ParcelAlreadyTrackedException(Parcel parcel) {
            super(String.format("Parcel %s is already tracked", parcel.getId()));
        }
    }
}
