import React, { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';

const JobList = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const [jobs, setJobs] = useState([]);

  useEffect(() => {
    const fetchJobs = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8080/api/jobs/view', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setJobs(response.data);
      } catch (error) {
        console.error('Error fetching jobs:', error);
      }
    };

    fetchJobs();
  }, []);

  const handleApplyRedirect = () => {
    if (!user) {
      navigate('/login');
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-2xl font-bold mb-4">Available Jobs</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {jobs.map((job) => (
          <div key={job.id} className="border p-4 rounded shadow bg-white">
            <h3 className="text-xl font-semibold">{job.title}</h3>
            <p>Company: {job.company}</p>
            <p>Location: {job.location}</p>
            <p>Salary: {job.salary}</p>
            <p>Type: {job.jobType}</p>
            <p className="text-sm text-gray-600">Posted by: {job.postedBy?.email}</p>

            {/* ✅ Show the 'about' field */}
            {job.about && (
              <p className="text-gray-700 mt-2">
                <span className="font-semibold">About:</span>{' '}
                {job.about.length > 150 ? `${job.about.substring(0, 150)}...` : job.about}
              </p>
            )}

            <div className="mt-2 flex gap-4">
              <Link
                to={`/jobs/${job.id}`}
                className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700"
              >
                View Details
              </Link>
              {user?.role === 'EMPLOYEE' ? (
                <Link
                  to={`/jobs/${job.id}`}
                  className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700"
                >
                  Apply
                </Link>
              ) : (
                <button
                  onClick={handleApplyRedirect}
                  className="bg-gray-500 text-white px-3 py-1 rounded hover:bg-gray-600"
                >
                  Apply
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default JobList;


