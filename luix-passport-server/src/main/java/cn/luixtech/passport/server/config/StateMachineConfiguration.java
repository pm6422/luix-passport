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
                .end(UserState.ENABLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<UserState, UserEvent> transitions) throws Exception {
        transitions
                .withExternal().source(UserState.CREATED).target(UserState.INACTIVATED).event(UserEvent.CREATE)
                .and()
                .withExternal().source(UserState.INACTIVATED).target(UserState.ACTIVATED).event(UserEvent.ACTIVATE);
    }
}