import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import logo from '../assets/logo.png';

function Header() {
    const { user, isAuthenticated, logout, isAdmin } = useAuth();
    const [showMenu, setShowMenu] = useState(false);
    const navigate = useNavigate();

    return (
        <header className="flex justify-between items-center bg-white py-3 px-8 shadow-md">
            <div className="flex items-center gap-2">
                <Link to="/">
                    <img src={logo} alt="Logo" className="h-16" />
                </Link>
            </div>

            <div className="flex items-center gap-4 relative">
                {!isAuthenticated ? (
                    <>
                        <Link to="/register" className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-900 transition">
                            Sign up
                        </Link>
                        <Link to="/login" className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-900 transition">
                            Sign in
                        </Link>
                    </>
                ) : (
                    <>
                        <div className="relative">
                            <div
                                className="w-10 h-10 flex items-center justify-center rounded-full bg-gray-300 cursor-pointer"
                                onClick={() => setShowMenu(!showMenu)}
                            >
                                {user?.profileImage ? (
                                    <img src={user.profileImage} alt="Profile" className="w-full h-full rounded-full object-cover" />
                                ) : (
                                    <span className="text-lg font-bold text-black">
                                        {user?.name?.charAt(0).toUpperCase()}
                                    </span>
                                )}
                            </div>

                            {showMenu && (
                                <div className="absolute right-0 mt-2 w-48 bg-white border rounded-lg shadow-lg">
                                    <div
                                        className="px-4 py-2 border-b text-black font-bold cursor-pointer hover:bg-gray-200"
                                        onClick={() => navigate('/profile')}
                                    >
                                        {user?.name}
                                    </div>

                                    {isAdmin && (
                                        <Link
                                            to="/users"
                                            className="block px-4 py-2 text-gray-700 hover:bg-gray-200 transition cursor-pointer"
                                        >
                                            Users
                                        </Link>
                                    )}

                                    <button
                                        onClick={() => logout(navigate)}
                                        className="w-full text-left px-4 py-2 text-red-500 hover:bg-red-100 transition"
                                    >
                                        Sign out
                                    </button>
                                </div>
                            )}
                        </div>
                    </>
                )}
            </div>
        </header>
    );
}

export default Header;