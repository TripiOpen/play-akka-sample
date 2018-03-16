import com.google.inject.AbstractModule;
import java.time.Clock;

import com.google.inject.name.Names;
import services.ApplicationTimer;
import services.AtomicCounter;
import services.Counter;
import services.booking.BookingService;
import services.booking.BookingTicketServiceAsyncImpl;
import services.booking.BookingTicketServiceAkkaImpl;
import services.booking.BookingTicketServiceSyncImpl;
import services.log.BookingLogServiceImpl;
import services.log.LogService;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        // Use the system clock as the default implementation of Clock
        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
        bind(ApplicationTimer.class).asEagerSingleton();
        // Set AtomicCounter as the implementation for Counter.
        bind(Counter.class).to(AtomicCounter.class);

        /**
         * Booking Binding
         */
        bind(BookingService.class)
                .annotatedWith(Names.named("bookingAsync"))
                .to(BookingTicketServiceAsyncImpl.class);

        bind(BookingService.class)
                .annotatedWith(Names.named("bookingSync"))
                .to(BookingTicketServiceSyncImpl.class);

        bind(BookingService.class)
                .annotatedWith(Names.named("bookingAkka"))
                .to(BookingTicketServiceAkkaImpl.class);

        /**
         * Log Binding
         */
        bind(LogService.class)
                .annotatedWith(Names.named("bookingLog"))
                .to(BookingLogServiceImpl.class);
    }

}
