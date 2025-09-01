package manage;

import jobs.ComposeJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import report.Reporter;
import java.util.Properties;
import java.util.function.Consumer;

public class ScheduleManager {
    private Scheduler scheduler;
    private final Consumer<String> log;
    private final Reporter reporter;

    public ScheduleManager(Consumer<String> log, Reporter reporter) {
        this.log = log; this.reporter = reporter;
    }

    public void start() throws Exception {
        if (scheduler == null) {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            log.accept("⏱ Planlayıcı başlatıldı.");
        }
    }

    public void scheduleCompose(Properties props, String text, String replyTo, String quoteId, String cron) throws Exception {
        start();
        JobDetail job = JobBuilder.newJob(ComposeJob.class).withIdentity("compose-"+System.currentTimeMillis(), "compose").build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("tr-compose-"+System.currentTimeMillis(), "compose")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        ComposeJob.LOGGER = log;
        ComposeJob.PROPS = props;
        ComposeJob.REPORTER = reporter;
        ComposeJob.TEXT = text;
        ComposeJob.REPLY_TO = replyTo;
        ComposeJob.QUOTE_ID = quoteId;

        scheduler.scheduleJob(job, trigger);
        log.accept("📆 Planlandı: compose -> " + cron);
    }

    public void stopAll() throws Exception {
        if (scheduler != null) {
            scheduler.shutdown(true);
            scheduler = null;
            log.accept("🛑 Tüm planlar durduruldu.");
        }
    }
}