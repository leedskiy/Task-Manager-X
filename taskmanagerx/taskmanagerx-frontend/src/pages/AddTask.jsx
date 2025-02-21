import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';

const AddTask = () => {
    const { isAdmin } = useAuth();
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [dueDate, setDueDate] = useState('');
    const [selectedUserId, setSelectedUserId] = useState('');
    const [users, setUsers] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (isAdmin) {
            api.get('/admin/users')
                .then((response) => setUsers(response.data))
                .catch((err) => console.error("Failed to fetch users", err));
        }
    }, [isAdmin]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        const formattedDueDate = `${dueDate}T00:00:00`;
        let requestData = {
            title,
            description,
            dueDate: formattedDueDate,
        };

        if (isAdmin) {
            if (!selectedUserId) {
                setError('Please select a user to assign the task.');
                return;
            }
            requestData = { ...requestData, userId: selectedUserId };
        }

        try {
            const endpoint = isAdmin ? '/admin/tasks' : '/tasks';
            const response = await api.post(endpoint, requestData);

            if (response.status === 200) {
                setSuccess('Task added successfully!');
                setTimeout(() => navigate('/'), 700);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to add task');
        }
    };

    const handleCancel = () => {
        navigate('/');
    };

    return (
        <div className="min-h-screen flex flex-col">
            <Header />

            <div className="flex-grow flex items-center justify-center px-4">
                <div className="flex flex-col items-center w-full max-w-md">
                    <h2 className="text-3xl font-bold text-gray-700 mb-6 text-center">
                        {isAdmin ? 'Create and Assign a Task' : 'Add a New Task'}
                    </h2>
                    <form
                        onSubmit={handleSubmit}
                        className="bg-white shadow-md rounded px-8 pt-6 pb-8 w-96"
                    >
                        <div className="h-2 mb-4 text-center">
                            {error && (
                                <div className="text-red-500 text-sm font-bold">
                                    {error}
                                </div>
                            )}
                            {success && (
                                <div className="text-green-500 text-sm font-bold">
                                    {success}
                                </div>
                            )}
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2">Title</label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3"
                                type="text"
                                placeholder="Task Title"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2">Description</label>
                            <textarea
                                className="shadow appearance-none border rounded w-full py-2 px-3"
                                placeholder="Task Description"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                rows="4"
                            />
                        </div>

                        {isAdmin && (
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Assign to User</label>
                                <select
                                    className="shadow appearance-none border rounded w-full py-2 px-3"
                                    value={selectedUserId}
                                    onChange={(e) => setSelectedUserId(e.target.value)}
                                    required
                                >
                                    <option value="">Select a User</option>
                                    {users.map((user) => (
                                        <option key={user.id} value={user.id}>
                                            {user.email}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        )}

                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2">Due Date</label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3"
                                type="date"
                                value={dueDate}
                                onChange={(e) => setDueDate(e.target.value)}
                                required
                            />
                        </div>

                        <div className="flex justify-between mt-6">
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="bg-gray-400 hover:bg-gray-500 text-white font-bold py-2 px-4 rounded"
                            >
                                Cancel
                            </button>

                            <button
                                type="submit"
                                className="bg-gray-700 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded"
                            >
                                {isAdmin ? 'Create Task' : 'Add Task'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AddTask;
