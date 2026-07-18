import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// Global Fetch Interceptor for Network Failures
const originalFetch = window.fetch;
window.fetch = async (...args) => {
  try {
    const response = await originalFetch(...args);


    if (response.status === 502 || response.status === 503 || response.status === 504) {
      const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
      const msg = isLocal
        ? 'Unable to connect to the server. Please ensure your backend server is turned on and running.'
        : 'We are having trouble connecting to the service. Please check your internet connection or try again later.';

      window.dispatchEvent(new CustomEvent('network-error', { detail: { message: msg } }));
      throw new Error(msg);
    }

    // Wrap response.json() so if parsing fails, because of gateway HTML payload, it throws our clean error
    if (!response.ok) {
      const originalJson = response.json.bind(response);
      response.json = async () => {
        try {
          return await originalJson();
        } catch (e) {
          const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
          throw new Error(isLocal
            ? 'Unable to connect to the server. Please ensure your backend server is turned on and running.'
            : 'We are having trouble connecting to the service. Please check your internet connection or try again later.'
          );
        }
      };
    }

    // Notify application that network operations are working
    window.dispatchEvent(new CustomEvent('network-success'));
    return response;
  } catch (error) {
    const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    const msg = isLocal
      ? 'Unable to connect to the server. Please ensure your backend server is turned on and running.'
      : 'We are having trouble connecting to the service. Please check your internet connection or try again later.';

    if (
      error.message === msg ||
      error instanceof TypeError ||
      error.message.includes('Failed to fetch') ||
      error.message.includes('Failed') ||
      error.message.includes('NetworkError') ||
      error.message.includes('network')
    ) {
      window.dispatchEvent(new CustomEvent('network-error', { detail: { message: msg } }));
      throw new Error(msg);
    }
    throw error;
  }
};

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
