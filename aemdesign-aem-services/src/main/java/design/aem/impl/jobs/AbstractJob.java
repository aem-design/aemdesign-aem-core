//package design.aem.impl.jobs;
//
//
//import org.apache.felix.scr.annotations.*;
//import org.apache.sling.commons.scheduler.ScheduleOptions;
//import org.apache.sling.commons.scheduler.Scheduler;
//import org.osgi.service.component.ComponentContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Objects;
//
//@Component(immediate = true)
//@Service(value = Runnable.class)
//public abstract class AbstractJob implements Runnable {
//
//    protected static final int UNLIMITED = -1;
//
//    protected final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    @Reference
//    private Scheduler scheduler;
//
//    protected String getJobName() {
//        return getClass().getName();
//    }
//
//    protected String getJobDescription() {
//        return getJobName();
//    }
//
//    protected boolean isEnabled() {
//        return true;
//    }
//
//    protected abstract ScheduleOptions provideScheduleOptions(Scheduler scheduler);
//
//    @Activate
//    protected synchronized void activate(ComponentContext componentContext) {
//        if (isEnabled()) {
//            ScheduleOptions scheduleOptions = provideScheduleOptions(scheduler);
//            if (scheduleOptions == null) {
//                return;
//            }
//
//            String jobName = getJobName();
//            if (jobName == null) {
//                throw new NullPointerException("job name of a class " + this.getClass().getName() + " == null");
//            }
//
//            log.debug("Registering job: {}", toString());
//            scheduleOptions.name(jobName);
//            scheduler.unschedule(jobName);
//            scheduler.schedule(this, scheduleOptions);
//        }
//    }
//
//    @Deactivate
//    protected synchronized void deactivate(ComponentContext componentContext) {
//        log.debug("Unregistering job: {}", toString());
//        scheduler.unschedule(getJobName());
//    }
//
//    @Override
//    public final void run() {
//        log.debug("Triggered job: " + toString());
//
//        try {
//            triggered();
//            log.debug("Job run completed: {}", toString());
//        } catch (Exception e) {
//            log.error("Job run failed: {}", toString(), e);
//        }
//    }
//
//    public abstract void triggered() throws Exception;
//
//    @Override
//    public String toString() {
//        if (Objects.equals(getJobName(), getJobDescription()) || getJobDescription() == null) {
//            return getJobName();
//        } else {
//            return getJobDescription() + " [" + getJobName() + "]";
//        }
//    }
//}
