import { useParams, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Header from '../components/Header';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const ModifyTask = () => {
    const { taskId } = useParams();
    const navigate = useNavigate();
    const { isAdmin } = useAuth();

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [status, setStatus] = useState('');
    const [dueDate, setDueDate] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchTask = async () => {
            try {
                const endpoint = isAdmin ? `/admin/tasks/${taskId}` : `/tasks/${taskId}`;
                const response = await api.get(endpoint);
                const task = response.data;

                setTitle(task.title);
                setDescription(task.description);
                setStatus(task.status);
                setDueDate(task.dueDate?.split('T')[0] || '');
            } catch (err) {
                setError('Task not found');
            } finally {
                setLoading(false);
            }
        };

        fetchTask();
    }, [taskId, isAdmin]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        const formattedDueDate = dueDate ? new Date(dueDate).toISOString() : null;
        const endpoint = isAdmin ? `/admin/tasks/${taskId}` : `/tasks/${taskId}`;

        try {
            const response = await api.put(endpoint, {
                title,
                description,
                status,
                dueDate: formattedDueDate,
            });

            if (response.status === 200) {
                setSuccess('Task updated successfully!');
                setTimeout(() => navigate('/'), 700);
            }
        } catch (error) {
            setError('Failed to update the task.');
        }
    };

    const handleCancel = () => {
        navigate('/');
    };

    if (loading) {
        return <div className="text-center text-gray-500">Loading...</div>;
    }

    return (
        <div className="min-h-screen flex flex-col">
            <Header />
            <div className="flex-grow flex items-center justify-center px-4">
                <div className="flex flex-col items-center w-full max-w-md">
                    <h2 className="text-3xl font-bold text-gray-700 mb-6 text-center">Modify Task</h2>

                    <form onSubmit={handleSubmit} className="bg-white shadow-md rounded px-8 pt-6 pb-8 w-96">
                        <div className="h-2 mb-4 text-center">
                            {error && <div className="text-red-500 text-sm font-bold">{error}</div>}
                            {success && <div className="text-green-500 text-sm font-bold">{success}</div>}
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2">Title</label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3"
                                type="text"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2">Description</label>
                            <textarea
                                className="shadow appearance-none border rounded w-full py-2 px-3"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                rows="4"
                            />
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2">Status</label>
                            <select
                                className="shadow appearance-none border rounded w-full py-2 px-3"
                                value={status}
                                onChange={(e) => setStatus(e.target.value)}
                                required
                            >
                                <option value="">Select Status</option>
                                <option value="PENDING">Pending</option>
                                <option value="COMPLETED">Completed</option>
                            </select>
                        </div>

                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2">Due Date</label>
                            <input
                                className="shadow appearance-none border rounded w-full py-2 px-3"
                                type="date"
                                value={dueDate}
                                onChange={(e) => setDueDate(e.target.value)}
                            />
                        </div>

                        <div className="flex justify-between mt-6">
                            <button
                                type="submit"
                                className="bg-gray-700 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded"
                            >
                                Save Changes
                            </button>
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="bg-gray-400 hover:bg-gray-500 text-white font-bold py-2 px-4 rounded"
                            >
                                Cancel
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ModifyTask;