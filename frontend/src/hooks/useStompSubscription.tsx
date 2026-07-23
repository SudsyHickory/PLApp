import { useEffect } from "react";
import { useWebSocket } from "../context/WebSocketContext";


export function useStompSubscription<T>(topic: string, onMessage: (data:T) => void)
{
    const {stompClient, isConnected} = useWebSocket();

     useEffect(()=>{
    
            if(!isConnected || !stompClient)
                return;
    
            const subscription = stompClient.subscribe(topic, (message) => {
                const data  = JSON.parse(message.body);
                onMessage(data);
            });
        
            return () => subscription.unsubscribe();
    
    }, [isConnected, stompClient]);
}