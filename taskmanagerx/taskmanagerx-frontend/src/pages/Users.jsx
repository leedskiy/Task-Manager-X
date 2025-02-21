import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';

const Users = () => {
    const { isAdmin } = useAuth();
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!isAdmin) {
            navigate('/');
        } else {
            fetchUsers();
        }
    }, [isAdmin, navigate]);

    const fetchUsers = async () => {
        try {
            const response = await api.get('/admin/users');
            setUsers(response.data);
        } catch (error) {
            setError('Failed to fetch users');
        } finally {
            setLoading(false);
        }
    };

    const deleteUser = async (userId) => {
        if (!window.confirm("Are you sure you want to delete this user? This action cannot be undone.")) {
            return;
        }

        try {
            await api.delete(`/admin/users/${userId}`);
            setUsers(users.filter(user => user.id !== userId));
        } catch (error) {
            console.error("Failed to delete user:", error);
            alert("Error: Unable to delete user.");
        }
    };

    const formatDate = (dateString) => {
        return new Date(dateString)
            .toLocaleDateString("en-US", {
                month: "2-digit",
                day: "2-digit",
                year: "numeric",
            })
            .replace(/\//g, ".");
    };

    return (
        <div className="min-h-screen flex flex-col">
            <Header />
            <div className="flex-grow flex flex-col items-center px-6">
                <h2 className="text-3xl font-bold text-gray-700 my-6">Users</h2>

                {loading ? (
                    <p className="text-gray-500">Loading users...</p>
                ) : error ? (
                    <p className="text-red-500">{error}</p>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-6xl">
                        {users.map(user => (
                            <div key={user.id} className="bg-white p-4 border rounded-lg shadow-md flex flex-col items-center">
                                <div className="w-20 h-20 flex items-center justify-center rounded-full bg-gray-300 overflow-hidden">
                                    {user.profileImage ? (
                                        <img src={user.profileImage} alt="User" className="w-full h-full object-cover" />
                                    ) : (
                                        <span className="text-2xl font-bold text-black">
                                            {user.name.charAt(0).toUpperCase()}
                                        </span>
                                    )}
                                </div>

                                <h3 className="text-lg font-bold text-gray-800 mt-2">{user.name}</h3>

                                <p className="text-gray-500 text-sm">
                                    {user.email}
                                </p>

                                <p className="text-gray-500 text-sm mt-1">
                                    <span className="font-semibold">Joined: </span>
                                    {formatDate(user.createdAt)}
                                </p>

                                <p className="text-gray-500 text-sm">
                                    <span className="font-semibold">Auth Provider: </span>
                                    {user.authProvider.toLowerCase()}
                                </p>

                                <p className="text-gray-500 text-sm">
                                    <span className="font-semibold">Roles: </span>
                                    {user.roles.length > 0
                                        ? user.roles.map(role => role === "ROLE_ADMIN" ? "admin" : role === "ROLE_USER" ? "user" : role).join(', ')
                                        : 'No roles assigned'}
                                </p>

                                {user.roles.includes("ROLE_ADMIN") ? (
                                    <p className="text-gray-400 font-semibold mt-3">Admin cannot be deleted</p>
                                ) : (
                                    <button
                                        onClick={() => deleteUser(user.id)}
                                        className="mt-4 px-4 py-2 bg-gray-400 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                                    >
                                        Delete User
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Users;
