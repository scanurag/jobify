import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Login from './components/Login';
import Signup from './components/Signup';
import JobList from './components/JobList';
import JobDetails from './components/JobDetails';
import JobForm from './components/JobForm';
import Dashboard from './components/Dashboard';

function App() {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-gray-100">
        <Navbar />
        <div className="text-3xl font-bold text-blue-600 underline p-4">
        </div>
        <Routes
          future={{
            v7_startTransition: true,
            v7_relativeSplatPath: true,
          }}
        >
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/jobs" element={<JobList />} />
          <Route path="/jobs/:id" element={<JobDetails />} />
          <Route path="/post-job" element={<JobForm />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </div>
    </AuthProvider>
  );
}

export default App;


