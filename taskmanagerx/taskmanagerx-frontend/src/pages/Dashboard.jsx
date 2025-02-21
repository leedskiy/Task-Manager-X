import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import Header from "../components/Header";
import TaskFilterSortBar from "../components/TaskFilterSortBar";
import ConfirmModal from "../components/ConfirmModal";

function Dashboard() {
    const { user, isAuthenticated } = useAuth();
    const [tasks, setTasks] = useState([]);
    const isAdmin = user?.roles.includes("ROLE_ADMIN");

    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [taskToDelete, setTaskToDelete] = useState(null);

    const fetchTasks = async () => {
        try {
            const endpoint = isAdmin ? "/admin/tasks" : "/tasks";
            const response = await api.get(endpoint);
            setTasks(response.data);
        } catch (error) {
            console.error("Failed to fetch tasks:", error);
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
            console.error("Failed to update task status:", error);
        }
    };

    useEffect(() => {
        if (isAuthenticated) {
            fetchTasks();
        }
    }, [isAuthenticated, isAdmin]);

    const handleDeleteClick = (task) => {
        setTaskToDelete(task);
        setIsDeleteModalOpen(true);
    };

    const confirmDeleteTask = async () => {
        if (!taskToDelete) return;

        try {
            const endpoint = isAdmin ? `/admin/tasks/${taskToDelete.id}` : `/tasks/${taskToDelete.id}`;
            await api.delete(endpoint);
            setTasks(tasks.filter(task => task.id !== taskToDelete.id));
        } catch (error) {
            console.error("Failed to delete task:", error);
        } finally {
            setIsDeleteModalOpen(false);
            setTaskToDelete(null);
        }
    };

    return (
        <div>
            <Header />
            <div className="p-8 text-center">
                <h1 className="text-3xl text-gray-700 font-bold mb-6">Dashboard</h1>

                {isAuthenticated && <TaskFilterSortBar setTasks={setTasks} isAdmin={isAdmin} />}

                <div className="mt-10">
                    {isAuthenticated ? (
                        <>
                            <Link
                                to="/add-task"
                                className="px-6 py-3 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                            >
                                Add a Task
                            </Link>

                            <div className="mt-10 mx-auto max-w-6xl">
                                <div className="flex flex-wrap gap-4 justify-center">
                                    {tasks.length > 0 ? (
                                        tasks.map((task) => (
                                            <div
                                                key={task.id}
                                                className={`w-64 h-64 border-2 rounded-lg shadow-md p-4 bg-white flex flex-col ${task.status === "PENDING"
                                                    ? "border-yellow-500"
                                                    : task.status === "COMPLETED"
                                                        ? "border-green-500"
                                                        : "border-gray-300"
                                                    }`}
                                            >
                                                <Link
                                                    to={`/tasks/${task.id}`}
                                                    className="text-lg font-bold text-gray-800 truncate hover:cursor-pointer"
                                                >
                                                    {task.title.length > 25
                                                        ? `${task.title.substring(0, 25)}...`
                                                        : task.title}
                                                </Link>

                                                <p className="text-gray-500 mt-2 text-sm">
                                                    Due:{" "}
                                                    {new Date(task.dueDate)
                                                        .toLocaleDateString("en-US", {
                                                            month: "2-digit",
                                                            day: "2-digit",
                                                            year: "numeric",
                                                        })
                                                        .replace(/\//g, ".")}
                                                </p>

                                                {isAdmin && (
                                                    <p className="text-gray-500 mt-2 text-sm">
                                                        Assigned to: <br /> {task.user.email}
                                                    </p>
                                                )}

                                                <p className="text-gray-600 text-sm mt-2 overflow-hidden">
                                                    {task.description?.length > 80
                                                        ? `${task.description.substring(0, 80)}...`
                                                        : task.description}
                                                </p>

                                                <div className={`flex ${isAdmin ? "justify-center" : "justify-between"} mt-auto`}>
                                                    <button
                                                        onClick={() => handleDeleteClick(task)}
                                                        className="px-4 py-2 bg-gray-400 text-white font-semibold rounded-lg hover:bg-gray-600 transition"
                                                    >
                                                        Delete
                                                    </button>

                                                    {!isAdmin && (
                                                        <button
                                                            onClick={() => handleUpdateStatus(task.id, task.status)}
                                                            className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                                                        >
                                                            Status
                                                        </button>
                                                    )}
                                                </div>
                                            </div>
                                        ))
                                    ) : (
                                        <p className="text-gray-500 text-lg">
                                            {isAdmin ? "No tasks available." : "Start by adding a task."}
                                        </p>
                                    )}
                                </div>
                            </div>
                        </>
                    ) : (
                        <p className="text-xl text-gray-500 font-semibold mt-6">
                            Login to start using Task Manager X.
                        </p>
                    )}
                </div>
            </div>

            <ConfirmModal
                isOpen={isDeleteModalOpen}
                onClose={() => setIsDeleteModalOpen(false)}
                onConfirm={confirmDeleteTask}
                title="Delete Task"
                message={`Are you sure you want to delete task "${taskToDelete?.title}"? This action is irreversible.`}
            />
        </div>
    );
}

export default Dashboard;