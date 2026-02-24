import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './meeting-task.reducer';

export const MeetingTask = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const meetingTaskList = useAppSelector(state => state.meetingTask.entities);
  const loading = useAppSelector(state => state.meetingTask.loading);

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
      <h2 id="meeting-task-heading" data-cy="MeetingTaskHeading">
        <Translate contentKey="meetingsApp.meetingTask.home.title">Meeting Tasks</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="meetingsApp.meetingTask.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/meeting-task/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="meetingsApp.meetingTask.home.createLabel">Create new Meeting Task</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {meetingTaskList && meetingTaskList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="meetingsApp.meetingTask.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('type')}>
                  <Translate contentKey="meetingsApp.meetingTask.type">Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('type')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="meetingsApp.meetingTask.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="meetingsApp.meetingTask.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('dueAt')}>
                  <Translate contentKey="meetingsApp.meetingTask.dueAt">Due At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dueAt')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="meetingsApp.meetingTask.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('remindBeforeMinutes')}>
                  <Translate contentKey="meetingsApp.meetingTask.remindBeforeMinutes">Remind Before Minutes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('remindBeforeMinutes')} />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingTask.assignee">Assignee</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingTask.assignedBy">Assigned By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingTask.meeting">Meeting</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {meetingTaskList.map((meetingTask, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/meeting-task/${meetingTask.id}`} color="link" size="sm">
                      {meetingTask.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`meetingsApp.TaskType.${meetingTask.type}`} />
                  </td>
                  <td>{meetingTask.title}</td>
                  <td>{meetingTask.description}</td>
                  <td>{meetingTask.dueAt ? <TextFormat type="date" value={meetingTask.dueAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    <Translate contentKey={`meetingsApp.TaskStatus.${meetingTask.status}`} />
                  </td>
                  <td>{meetingTask.remindBeforeMinutes}</td>
                  <td>{meetingTask.assignee ? meetingTask.assignee.login : ''}</td>
                  <td>{meetingTask.assignedBy ? meetingTask.assignedBy.login : ''}</td>
                  <td>{meetingTask.meeting ? <Link to={`/meeting/${meetingTask.meeting.id}`}>{meetingTask.meeting.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/meeting-task/${meetingTask.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/meeting-task/${meetingTask.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/meeting-task/${meetingTask.id}/delete`)}
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
              <Translate contentKey="meetingsApp.meetingTask.home.notFound">No Meeting Tasks found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default MeetingTask;
