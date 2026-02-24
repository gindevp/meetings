import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Meeting from './meeting';
import MeetingDetail from './meeting-detail';
import MeetingUpdate from './meeting-update';
import MeetingDeleteDialog from './meeting-delete-dialog';

const MeetingRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Meeting />} />
    <Route path="new" element={<MeetingUpdate />} />
    <Route path=":id">
      <Route index element={<MeetingDetail />} />
      <Route path="edit" element={<MeetingUpdate />} />
      <Route path="delete" element={<MeetingDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MeetingRoutes;
