import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';

const JobDetails = () => {
  const { id } = useParams();
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const [job, setJob] = useState(null);
  const [resume, setResume] = useState(null);
  const [coverLetter, setCoverLetter] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/jobs/${id}`);
        setJob(response.data);
      } catch (error) {
        console.error('Error fetching job:', error); 
        setError('Unable to load job details');
      }
    };
    fetchJob();
  }, [id]);

  const handleApply = async (e) => {
    e.preventDefault();

    if (!user) {
      navigate('/login');
      return;
    }

    if (!resume) {
      setError('Please upload a resume');
      return;
    }

    const formData = new FormData();
    formData.append('resume', resume);
    formData.append('jobId', id);
    formData.append('email', user.email);
    formData.append('fullName', user.name);
    formData.append('phone', user.phone || 'NA'); // Add default if not available

    try {
      await axios.post('http://localhost:8080/api/apply', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      alert('Application submitted successfully!');
      navigate('/dashboard');
    } catch (error) {
      console.error('Error applying:', error);
      setError('Application submission failed');
    }
  };

  if (!job) return <div>Loading...</div>;

  return (
    <div className="container mx-auto p-4 max-w-2xl">
      <h2 className="text-2xl font-bold mb-4">{job.title}</h2>
      <p><strong>Company:</strong> {job.company}</p>
      <p><strong>Location:</strong> {job.location}</p>
      <p><strong>Salary:</strong> {job.salary}</p>
      <p><strong>Type:</strong> {job.jobType}</p>
      <p><strong>Posted By:</strong> {job.postedBy?.email}</p>

      {user?.role === 'EMPLOYEE' && (
        <div className="mt-6">
          <h3 className="text-xl font-semibold mb-2">Apply for this Job</h3>
          {error && <p className="text-red-500">{error}</p>}
          <form onSubmit={handleApply} className="space-y-4">
            <div>
              <label className="block text-sm font-medium">Resume</label>
              <input
                type="file"
                onChange={(e) => setResume(e.target.files[0])}
                className="w-full p-2 border rounded"
                accept=".pdf"
              />
            </div>
            <div>
              <label className="block text-sm font-medium">Cover Letter</label>
              <textarea
                value={coverLetter}
                onChange={(e) => setCoverLetter(e.target.value)}
                className="w-full p-2 border rounded"
                rows="4"
              />
            </div>
            <button
              type="submit"
              className="w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-700"
            >
              Apply
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default JobDetails;
