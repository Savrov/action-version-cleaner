package team.credible.action.versioncleaner

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

class KotestConfig : AbstractProjectConfig() {
    override val parallelism = 3
    override val isolationMode = IsolationMode.InstancePerTest
}
