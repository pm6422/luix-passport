package cn.luixtech.passport.server.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
public class StateMachineListener<S, E> extends StateMachineListenerAdapter<S, E> {
 
    @Override
    public void stateChanged(State<S, E> from, State<S, E> to) {
        System.out.printf("Transitioned from %s to %s%n", from == null ?
          "none" : from.getId(), to.getId());
    }
}