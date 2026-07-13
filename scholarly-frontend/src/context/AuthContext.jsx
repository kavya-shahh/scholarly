import React, { createContext, useState, useEffect, useContext } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Fetch the current user on component mount (resolving session cookie)
    useEffect(() => {
        const checkSession = async () => {
            try {
                const response = await fetch('/api/v1/auth/me', {
                    method: 'GET',
                    headers: { 'Accept': 'application/json' },
                    credentials: 'include'
                });

                if (response.ok) {
                    const data = await response.json();
                    setUser(data);
                } else {
                    setUser(null);
                }
            } catch (err) {
                console.error("Session check failed:", err);
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        checkSession();
    }, []);

    const login = async (email, password) => {
        setError(null);
        try {
            const response = await fetch('/api/v1/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
                credentials: 'include'
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Login failed');
            }

            const data = await response.json();
            setUser(data);
            return data;
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    const register = async (registerData) => {
        setError(null);
        try {
            const response = await fetch('/api/v1/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(registerData),
                credentials: 'include'
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Registration failed');
            }

            const data = await response.json();
            return data;
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    const logout = async () => {
        try {
            await fetch('/api/v1/auth/logout', {
                method: 'POST',
                credentials: 'include'
            });
        } catch (err) {
            console.error("Logout request failed:", err);
        } finally {
            setUser(null);
        }
    };

    return (
        <AuthContext.Provider value={{ user, loading, error, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
