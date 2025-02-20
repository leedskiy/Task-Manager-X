import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';
import { getTokenFromCookies } from '../api/axios';

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
            const response = await api.post('/auth/login', credentials);
            localStorage.setItem('token', response.data.token);
            setIsAuthenticated(true);
            await fetchUserProfile();
        } catch (error) {
            throw error.response?.data?.message || 'Login failed';
        }
    };

    const logout = async (navigate) => {
        try {
            await api.post('/auth/logout');
            localStorage.removeItem('token');
            setUser(null);
            setIsAuthenticated(false);
            setIsAdmin(false);
            navigate('/');
        } catch (error) {
            console.error('Failed to log out:', error);
        }
    };

    useEffect(() => {
        const token = localStorage.getItem('token') || getTokenFromCookies();
        if (token) {
            fetchUserProfile();
        } else {
            setLoading(false);
        }
    }, []);

    return (
        <AuthContext.Provider value={{ user, isAuthenticated, isAdmin, loading, login, logout, fetchUserProfile }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);