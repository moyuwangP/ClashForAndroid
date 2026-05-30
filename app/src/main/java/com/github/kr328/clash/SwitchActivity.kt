package com.github.kr328.clash

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.github.kr328.clash.design.R
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess

class OnActionHelper(
    config: TaskerPluginConfig<Unit>,
) : TaskerPluginConfigHelperNoOutputOrInput<OnActionRunner>(config) {
    override val runnerClass: Class<OnActionRunner>
        get() = OnActionRunner::class.java

    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("start clash service")
    }
}

class OffActionHelper(
    config: TaskerPluginConfig<Unit>,
) : TaskerPluginConfigHelperNoOutputOrInput<OffActionRunner>(config) {
    override val runnerClass: Class<OffActionRunner>
        get() = OffActionRunner::class.java

    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("shutdown clash service")
    }
}

class SwitchOnAction : Activity(), TaskerPluginConfigNoInput {
    override val context: Context
        get() = applicationContext

    private val taskerHelper by lazy { OnActionHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskerHelper.onCreate()
        taskerHelper.finishForTasker()
    }
}

class SwitchOffAction : Activity(), TaskerPluginConfigNoInput {
    override val context: Context
        get() = applicationContext

    private val taskerHelper by lazy { OffActionHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskerHelper.onCreate()
        taskerHelper.finishForTasker()
    }
}

class OnActionRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        val vpnRequest = context.startClashService()
        val message = if (vpnRequest == null) {
            "start clash service"
        } else {
            context.getString(R.string.unable_to_start_vpn)
        }

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        return TaskerPluginResultSucess()
    }
}

class OffActionRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        context.stopClashService()

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "shutdown clash service", Toast.LENGTH_LONG).show()
        }

        return TaskerPluginResultSucess()
    }
}
