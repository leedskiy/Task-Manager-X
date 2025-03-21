import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import Header from "../components/Header";
import ConfirmModal from "../components/ConfirmModal";

const Profile = () => {
    const { user, fetchUserProfile, logout, isAdmin } = useAuth();
    const navigate = useNavigate();

    const [name, setName] = useState(user?.name || '');
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [success, setSuccess] = useState('');
    const [passwordSuccess, setPasswordSuccess] = useState('');
    const [error, setError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [loading, setLoading] = useState(true);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

    useEffect(() => {
        if (!user) {
            navigate('/');
        } else {
            setName(user.name);
            setLoading(false);
        }
    }, [user, navigate]);

    const showMessage = (setMessage, message) => {
        setMessage(message);
        setTimeout(() => setMessage(''), 1000);
    };

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            const response = await api.put('/users/me/name', { name });

            if (response.status === 200) {
                showMessage(setSuccess, 'Profile updated successfully!');
                fetchUserProfile();
            }
        } catch (err) {
            showMessage(setError, err.response?.data?.message || 'Failed to update profile');
        }
    };

    const handlePasswordChange = async (e) => {
        e.preventDefault();
        setPasswordError('');
        setPasswordSuccess('');

        if (!oldPassword || !newPassword) {
            showMessage(setPasswordError, 'Both password fields are required');
            return;
        }

        try {
            const response = await api.put('/auth/me/password', {
                oldPassword,
                newPassword
            });

            if (response.status === 200) {
                showMessage(setPasswordSuccess, 'Password updated successfully!');
                setOldPassword('');
                setNewPassword('');
            }
        } catch (err) {
            showMessage(setPasswordError, err.response?.data?.message || 'Failed to update password');
        }
    };

    const handleDeleteAccount = async () => {
        try {
            await api.delete('/users/me');
            navigate('/register');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to delete account');
        } finally {
            setIsDeleteModalOpen(false);
        }
    };

    if (loading) return <div className="text-center text-gray-500">Loading...</div>;
    if (!user) return <div className="text-center text-gray-500">Redirecting...</div>;

    return (
        <div className="min-h-screen flex flex-col">
            <Header />

            <div className="flex-grow flex flex-col items-center justify-center px-4">
                <h2 className="text-3xl font-bold text-gray-700 mb-6 text-center">Profile</h2>

                <div className={`bg-white shadow-md rounded px-6 pt-6 pb-8 flex flex-col items-center 
                    ${user.authProvider === 'GOOGLE' ? 'w-full max-w-sm' : 'w-full max-w-2xl'}`}>

                    <div className="w-24 h-24 flex items-center justify-center rounded-full bg-gray-300 mb-4">
                        {user?.profileImage ? (
                            <img src={user.profileImage} alt="Profile" className="w-full h-full rounded-full object-cover" />
                        ) : (
                            <span className="text-5xl font-bold text-black select-none">{user?.name?.charAt(0).toUpperCase()}</span>
                        )}
                    </div>

                    <div className={`w-full ${user.authProvider !== 'GOOGLE' ? 'grid grid-cols-1 md:grid-cols-2 gap-6' : ''}`}>
                        <div>
                            <div className="h-2 mb-4 text-center">
                                {error && <div className="text-red-500 text-sm font-bold">{error}</div>}
                                {success && <div className="text-green-500 text-sm font-bold">{success}</div>}
                            </div>

                            <form onSubmit={handleUpdateProfile}>
                                <div className="mb-4">
                                    <label className="block text-gray-700 text-sm font-bold mb-2">Email</label>
                                    <input className="shadow appearance-none border rounded w-full py-2 px-3 bg-gray-200 cursor-not-allowed" type="email" value={user.email} disabled />
                                </div>

                                <div className="mb-4">
                                    <label className="block text-gray-700 text-sm font-bold mb-2">Name</label>
                                    <input className="shadow appearance-none border rounded w-full py-2 px-3" type="text" value={name} onChange={(e) => setName(e.target.value)} required />
                                </div>

                                <button type="submit" className="bg-gray-700 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded w-full">Save Changes</button>
                            </form>
                        </div>

                        {user.authProvider !== 'GOOGLE' ? (
                            <div>
                                <div className="h-2 mb-4 text-center">
                                    {passwordError && <div className="text-red-500 text-sm font-bold">{passwordError}</div>}
                                    {passwordSuccess && <div className="text-green-500 text-sm font-bold">{passwordSuccess}</div>}
                                </div>

                                <form onSubmit={handlePasswordChange}>
                                    <div className="mb-4">
                                        <label className="block text-gray-700 text-sm font-bold mb-2">Old Password</label>
                                        <input className="shadow appearance-none border rounded w-full py-2 px-3" type="password" placeholder="Enter old password" value={oldPassword} onChange={(e) => setOldPassword(e.target.value)} required />
                                    </div>

                                    <div className="mb-4">
                                        <label className="block text-gray-700 text-sm font-bold mb-2">New Password</label>
                                        <input className="shadow appearance-none border rounded w-full py-2 px-3" type="password" placeholder="Enter new password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
                                    </div>

                                    <button type="submit" className="bg-gray-700 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded w-full">Update Password</button>
                                </form>
                            </div>
                        ) : (
                            <div className="h-2"></div>
                        )}
                    </div>

                    {!isAdmin && (
                        <button
                            onClick={() => setIsDeleteModalOpen(true)}
                            className="mt-4 px-4 py-2 bg-gray-400 text-white font-semibold rounded-lg hover:bg-gray-600 transition w-full"
                        >
                            Delete Account
                        </button>
                    )}
                </div>
            </div>

            <ConfirmModal
                isOpen={isDeleteModalOpen}
                onClose={() => setIsDeleteModalOpen(false)}
                onConfirm={handleDeleteAccount}
                title="Delete Account"
                message="Are you sure you want to delete your account? This action is irreversible."
            />
        </div>
    );
};

export default Profile;
