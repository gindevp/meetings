import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MeetingTask from './meeting-task';
import MeetingTaskDetail from './meeting-task-detail';
import MeetingTaskUpdate from './meeting-task-update';
import MeetingTaskDeleteDialog from './meeting-task-delete-dialog';

const MeetingTaskRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MeetingTask />} />
    <Route path="new" element={<MeetingTaskUpdate />} />
    <Route path=":id">
      <Route index element={<MeetingTaskDetail />} />
      <Route path="edit" element={<MeetingTaskUpdate />} />
      <Route path="delete" element={<MeetingTaskDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MeetingTaskRoutes;
