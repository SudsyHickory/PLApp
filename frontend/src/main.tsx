import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { WebSocketProvider } from './context/WebSocketContext.tsx'
import { MatchdayContextProvider } from './context/MatchdayContext.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <WebSocketProvider>
      <MatchdayContextProvider>
         <App />
      </MatchdayContextProvider>
    </WebSocketProvider>
  </StrictMode>,
)
