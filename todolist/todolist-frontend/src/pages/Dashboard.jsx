import Header from '../components/Header';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useState, useEffect } from 'react';
import api from '../api/axios';

function Dashboard() {
    const { user, isAuthenticated, loading } = useAuth();
    const [tasks, setTasks] = useState([]);

    const fetchTasks = async () => {
        try {
            const response = await api.get('/tasks');
            setTasks(response.data);
        } catch (error) {
            console.error('Failed to fetch tasks:', error);
        }
    };

    const handleDeleteTask = async (taskId) => {
        try {
            await api.delete(`/tasks/${taskId}`);
            setTasks(tasks.filter(task => task.id !== taskId));
        } catch (error) {
            console.error('Failed to delete task:', error);
        }
    };

    const handleUpdateStatus = async (taskId, currentStatus) => {
        const newStatus = currentStatus === 'PENDING' ? 'COMPLETED' : 'PENDING';

        try {
            const response = await api.put(`/tasks/${taskId}/status`, { status: newStatus });
            const updatedTask = response.data;

            setTasks(tasks.map(task =>
                task.id === updatedTask.id ? { ...task, status: updatedTask.status } : task
            ));
        } catch (error) {
            console.error('Failed to update task status:', error);
        }
    };

    useEffect(() => {
        if (isAuthenticated) {
            fetchTasks();
        }
    }, [isAuthenticated]);

    return (
        <div>
            <Header />
            <div className="p-8 text-center">
                <h1 className="text-3xl text-gray-700 font-bold mb-6">Dashboard</h1>

                {isAuthenticated ? (
                    <>
                        <Link to="/add-task" className="px-6 py-3 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition">
                            Add a Task
                        </Link>

                        <div className="mt-10 mx-auto max-w-6xl">
                            <div className="flex flex-wrap gap-4 justify-center">
                                {tasks.length > 0 ? (
                                    tasks.map((task) => (
                                        <div
                                            key={task.id}
                                            className={`w-64 h-64 border-2 rounded-lg shadow-md p-4 bg-white flex flex-col ${task.status === 'PENDING' ? 'border-yellow-500' :
                                                task.status === 'COMPLETED' ? 'border-green-500' :
                                                    'border-gray-300'
                                                }`}
                                        >
                                            <Link to={`/tasks/${task.id}`} className="text-lg font-bold text-gray-800 truncate hover:cursor-pointer">
                                                {task.title.length > 25 ? `${task.title.substring(0, 25)}...` : task.title}
                                            </Link>

                                            <p className="text-gray-500 mt-2 text-sm">
                                                Due: {new Date(task.dueDate).toLocaleDateString('en-US', {
                                                    month: '2-digit',
                                                    day: '2-digit',
                                                    year: 'numeric'
                                                }).replace(/\//g, '.')}
                                            </p>

                                            <p className="text-gray-600 text-sm mt-2 overflow-hidden">
                                                {task.description?.length > 80 ? `${task.description.substring(0, 80)}...` : task.description}
                                            </p>

                                            <div className="flex justify-between mt-auto">
                                                <button
                                                    onClick={() => handleUpdateStatus(task.id, task.status)}
                                                    className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                                                >
                                                    Status
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteTask(task.id)}
                                                    className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <p className="text-gray-500 text-lg">Start by adding a task.</p>
                                )}

                            </div>
                        </div>
                    </>
                ) : (
                    <p className="text-xl text-gray-500 font-semibold mt-6">
                        Login into account to start using To Do List.
                    </p>
                )}
            </div>
        </div>
    );
}

export default Dashboard;
