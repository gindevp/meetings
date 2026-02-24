import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MeetingApproval from './meeting-approval';
import MeetingApprovalDetail from './meeting-approval-detail';
import MeetingApprovalUpdate from './meeting-approval-update';
import MeetingApprovalDeleteDialog from './meeting-approval-delete-dialog';

const MeetingApprovalRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MeetingApproval />} />
    <Route path="new" element={<MeetingApprovalUpdate />} />
    <Route path=":id">
      <Route index element={<MeetingApprovalDetail />} />
      <Route path="edit" element={<MeetingApprovalUpdate />} />
      <Route path="delete" element={<MeetingApprovalDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MeetingApprovalRoutes;
