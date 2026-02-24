import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './meeting-participant.reducer';

export const MeetingParticipant = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const meetingParticipantList = useAppSelector(state => state.meetingParticipant.entities);
  const loading = useAppSelector(state => state.meetingParticipant.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="meeting-participant-heading" data-cy="MeetingParticipantHeading">
        <Translate contentKey="meetingsApp.meetingParticipant.home.title">Meeting Participants</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="meetingsApp.meetingParticipant.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/meeting-participant/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="meetingsApp.meetingParticipant.home.createLabel">Create new Meeting Participant</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {meetingParticipantList && meetingParticipantList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="meetingsApp.meetingParticipant.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('role')}>
                  <Translate contentKey="meetingsApp.meetingParticipant.role">Role</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('role')} />
                </th>
                <th className="hand" onClick={sort('isRequired')}>
                  <Translate contentKey="meetingsApp.meetingParticipant.isRequired">Is Required</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isRequired')} />
                </th>
                <th className="hand" onClick={sort('attendance')}>
                  <Translate contentKey="meetingsApp.meetingParticipant.attendance">Attendance</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('attendance')} />
                </th>
                <th className="hand" onClick={sort('absentReason')}>
                  <Translate contentKey="meetingsApp.meetingParticipant.absentReason">Absent Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('absentReason')} />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingParticipant.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingParticipant.meeting">Meeting</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {meetingParticipantList.map((meetingParticipant, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/meeting-participant/${meetingParticipant.id}`} color="link" size="sm">
                      {meetingParticipant.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`meetingsApp.ParticipantRole.${meetingParticipant.role}`} />
                  </td>
                  <td>{meetingParticipant.isRequired ? 'true' : 'false'}</td>
                  <td>
                    <Translate contentKey={`meetingsApp.AttendanceStatus.${meetingParticipant.attendance}`} />
                  </td>
                  <td>{meetingParticipant.absentReason}</td>
                  <td>{meetingParticipant.user ? meetingParticipant.user.login : ''}</td>
                  <td>
                    {meetingParticipant.meeting ? (
                      <Link to={`/meeting/${meetingParticipant.meeting.id}`}>{meetingParticipant.meeting.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/meeting-participant/${meetingParticipant.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/meeting-participant/${meetingParticipant.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/meeting-participant/${meetingParticipant.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="meetingsApp.meetingParticipant.home.notFound">No Meeting Participants found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default MeetingParticipant;
