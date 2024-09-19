package cn.luixtech.passport.server.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
public class StateMachineListener extends StateMachineListenerAdapter {
 
    @Override
    public void stateChanged(State from, State to) {
        System.out.printf("Transitioned from %s to %s%n", from == null ?
          "none" : from.getId(), to.getId());
    }
}