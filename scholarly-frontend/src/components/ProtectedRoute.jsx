import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, allowedRoles }) => {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <div style={styles.spinnerContainer}>
                <div style={styles.spinner}></div>
                <p style={styles.loadingText}>Verifying session credentials...</p>
            </div>
        );
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return <Navigate to="/unauthorized" replace />;
    }

    return children;
};

const styles = {
    spinnerContainer: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        backgroundColor: '#121214',
        color: '#e1e1e6',
        fontFamily: 'system-ui, sans-serif'
    },
    spinner: {
        width: '40px',
        height: '40px',
        border: '4px solid #323238',
        borderTop: '4px solid #00b37e',
        borderRadius: '50%',
        animation: 'spin 1s linear infinite',
        marginBottom: '16px'
    },
    loadingText: {
        fontSize: '16px',
        fontWeight: '500'
    }
};

// Inject keyframe animation dynamically for the loader
if (typeof document !== 'undefined') {
    const styleSheet = document.createElement("style");
    styleSheet.type = "text/css";
    styleSheet.innerText = `
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    `;
    document.head.appendChild(styleSheet);
}

export default ProtectedRoute;
