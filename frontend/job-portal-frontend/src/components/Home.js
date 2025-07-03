import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div className="container mx-auto p-4">
      <h1 className="text-4xl font-bold text-center mb-8">Welcome to the Job Portal</h1>
      <p className="text-center mb-4">Find your dream job or post job openings with ease.</p>
      <div className="text-center">
        <Link to="/jobs" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
          Browse Jobs
        </Link>
      </div>
    </div>
  );
};

export default Home;