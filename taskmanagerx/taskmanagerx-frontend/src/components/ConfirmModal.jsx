import React from "react";

const ConfirmModal = ({ isOpen, onClose, onConfirm, title, message }) => {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-lg shadow-lg p-6 w-80 text-center">
                <h2 className="text-lg font-bold text-gray-800 ">{title}</h2>
                <p className="text-gray-600 mt-2">{message}</p>

                <div className="flex justify-between mt-6 gap-4">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-400 text-white font-semibold rounded-lg hover:bg-gray-600 transition"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 bg-gray-700 text-white font-semibold rounded-lg hover:bg-gray-900 transition"
                    >
                        Confirm
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;