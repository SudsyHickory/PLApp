import { createContext, useContext, useEffect, useState } from "react";
import { Client } from '@stomp/stompjs';


interface WebSocketProviderProps{
    children : React.ReactNode
}

interface WebSocketContextType {
    stompClient : Client | null,
    isConnected: boolean
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const WebSocketProvider = ({ children } : WebSocketProviderProps) => {
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [isConnected, setIsConnected] = useState<boolean>(false);

  useEffect(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8080/websocket'
    });

    client.onConnect = () => {
      setIsConnected(true);
    };

    client.onDisconnect = () => {
      setIsConnected(false);
    };

    client.activate();
    setStompClient(client);

    return () => {
      if (client) 
        client.deactivate();
    };
  }, []);

  return (
    <WebSocketContext.Provider value={{ stompClient, isConnected }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => {
    const context = useContext(WebSocketContext);
    if(!context) 
        throw new Error()
    return context;
}
    
    