import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);
    const [loading, setLoading] = useState(true);

    const fetchUserProfile = async () => {
        try {
            const response = await api.get('/auth/me');
            setUser(response.data);
            setIsAuthenticated(true);
            setIsAdmin(response.data.roles.includes("ROLE_ADMIN"));
        } catch (error) {
            setUser(null);
            setIsAuthenticated(false);
            setIsAdmin(false);
        } finally {
            setLoading(false);
        }
    };

    const login = async (credentials) => {
        try {
            await api.post('/auth/login', credentials);
            setIsAuthenticated(true);
            await fetchUserProfile();
        } catch (error) {
            throw error.response?.data?.message || 'Login failed';
        }
    };

    const logout = async (navigate) => {
        try {
            await api.post('/auth/logout');
            setUser(null);
            setIsAuthenticated(false);
            setIsAdmin(false);
            navigate('/');
        } catch (error) {
            console.error('Failed to log out:', error);
        }
    };

    useEffect(() => {
        fetchUserProfile();
    }, []);

    return (
        <AuthContext.Provider value={{ user, isAuthenticated, isAdmin, loading, login, logout, fetchUserProfile }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);