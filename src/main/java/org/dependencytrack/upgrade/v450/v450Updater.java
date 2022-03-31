/*
 * This file is part of Dependency-Track.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package org.dependencytrack.upgrade.v450;

import alpine.Config;
import alpine.common.logging.Logger;
import alpine.persistence.AlpineQueryManager;
import alpine.server.upgrade.AbstractUpgradeItem;
import alpine.server.upgrade.UpgradeException;
import alpine.server.util.DbUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.dependencytrack.auth.Permissions;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class v450Updater extends AbstractUpgradeItem {

    private static final Logger LOGGER = Logger.getLogger(v450Updater.class);
    private static final String STMT_1 = "UPDATE \"VULNERABILITY\" SET \"CWE\" = NULL";

    @Override
    public String getSchemaVersion() {
        return "4.5.0";
    }

    @Override
    public void executeUpgrade(final AlpineQueryManager qm, final Connection connection) throws Exception {
        LOGGER.info("Deleting NIST directory");
        try {
            final String NIST_ROOT_DIR = Config.getInstance().getDataDirectorty().getAbsolutePath() + File.separator + "nist";
            FileDeleteStrategy.FORCE.delete(new File(NIST_ROOT_DIR));
        } catch (IOException e) {
            LOGGER.error("An error occurred deleting the NIST directory", e);
        }

        LOGGER.info("Clearing vulnerability CWEs. CWEs will be recreated when vulnerabilities are next synchronized.");
        DbUtil.executeUpdate(connection, STMT_1);
    }
}