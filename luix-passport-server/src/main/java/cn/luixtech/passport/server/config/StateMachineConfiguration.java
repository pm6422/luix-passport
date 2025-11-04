package cn.luixtech.passport.server.config;

import cn.luixtech.passport.server.statemachine.UserEvent;
import cn.luixtech.passport.server.statemachine.UserState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * refer to https://www.baeldung.com/spring-state-machine
 */
@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<UserState, UserEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<UserState, UserEvent> states) throws Exception {
        states
                .withStates()
                .initial(UserState.CREATED)
                .state(UserState.INACTIVATED)
                .state(UserState.ACTIVATED)
                .state(UserState.DISABLED)
                .state(UserState.ENABLED)
                .end(UserState.ENABLED); // 结束状态应该是已定义的状态
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<UserState, UserEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(UserState.CREATED)
                .target(UserState.INACTIVATED)
                .event(UserEvent.CREATE)
                .and()
                .withExternal()
                .source(UserState.INACTIVATED)
                .target(UserState.ACTIVATED)
                .event(UserEvent.ACTIVATE)
                .and()
                .withExternal()
                .source(UserState.ACTIVATED)
                .target(UserState.DISABLED)
                .event(UserEvent.DISABLE)
                .and()
                .withExternal()
                .source(UserState.DISABLED)
                .target(UserState.ENABLED)
                .event(UserEvent.ENABLE);
    }
}