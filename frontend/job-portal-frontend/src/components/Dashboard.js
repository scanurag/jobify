import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';

const Dashboard = () => {
  const { user } = useContext(AuthContext);
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);

  useEffect(() => {
    if (!user) return;

    const fetchData = async () => {
      try {
        if (user.role === 'HR') {
          // Fetch posted jobs
          const jobResponse = await axios.get('http://localhost:8080/api/jobs/view');
          setJobs(jobResponse.data.filter((job) => job.postedBy.email === user.email));

          // Fetch applications received for jobs posted by HR
          const appResponse = await axios.get(`http://localhost:8080/api/crm/applications/hr?email=${user.email}`);
          setApplications(appResponse.data);
        } else if (user.role === 'EMPLOYEE') {
          // Fetch applications submitted by employee
          const response = await axios.get('http://localhost:8080/api/crm/applications');
          setApplications(response.data);
        }
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      }
    };

    fetchData();
  }, [user]);

  const handleDelete = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/jobs/${id}`);
      setJobs(jobs.filter((job) => job.id !== id));
    } catch (error) {
      console.error('Error deleting job:', error);
    }
  };

  if (!user) return <div className="p-4">Please login to view your dashboard</div>;

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-2xl font-bold mb-4">Dashboard ({user.role})</h2>

      {user.role === 'HR' ? (
        <>
          <h3 className="text-xl font-semibold mb-2">Your Posted Jobs</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
            {jobs.map((job) => (
              <div key={job.id} className="border p-4 rounded shadow">
                <h4 className="text-lg font-semibold">{job.title}</h4>
                <p>Company: {job.company}</p>
                <p>Location: {job.location}</p>
                <button
                  onClick={() => handleDelete(job.id)}
                  className="text-red-600 hover:underline mt-2"
                >
                  Delete
                </button>
              </div>
            ))}
          </div>

          <h3 className="text-xl font-semibold mb-2">Applications Received</h3>
          <div className="space-y-4">
            {applications.map((app) => (
              <div key={app.id} className="border p-4 rounded shadow">
                <h4 className="text-lg font-semibold">{app.job.title}</h4>
                <p>Applicant: {app.user.name}</p>
                <p>Email: {app.user.email}</p>
                <p>Phone: {app.coverLetter?.split('Phone:')[1]}</p>
                <p>Status: {app.status}</p>
                <p>Applied On: {app.applyDate}</p>
                <a
  href={`http://localhost:8080/api/resume/download/${app.resumeUrl.replace('/uploads/', '')}`}
  download
  className="text-blue-600 hover:underline mt-2 inline-block"
>
  Download Resume
</a>


              </div>
            ))}
          </div>
        </>
      ) : (
        <>
          <h3 className="text-xl font-semibold mb-2">Your Applications</h3>
          <div className="space-y-4">
            {applications.map((app) => (
              <div key={app.id} className="border p-4 rounded shadow">
                <h4 className="text-lg font-semibold">{app.job.title}</h4>
                <p>Company: {app.job.company}</p>
                <p>Status: {app.status}</p>
                <p>Applied On: {app.applyDate}</p>
<a
  href={`http://localhost:8080/api/resume/download/${app.resumeUrl}`}
  target="_blank"
  rel="noopener noreferrer"
  className="text-blue-600 hover:underline mt-2 inline-block"
>
  View Submitted Resume
</a>

              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default Dashboard;

