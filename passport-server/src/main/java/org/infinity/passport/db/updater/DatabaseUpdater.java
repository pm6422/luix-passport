package org.infinity.passport.db.updater;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;

@ChangeLog(order = "02")
public class DatabaseUpdater {

    @ChangeSet(order = "01", author = "Louis", id = "data-updater")
    public void dataUpdater(MongockTemplate mongoTemplate) {
        // leave blank intentionally
    }
}
