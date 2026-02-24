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

import { getEntities } from './meeting-approval.reducer';

export const MeetingApproval = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const meetingApprovalList = useAppSelector(state => state.meetingApproval.entities);
  const loading = useAppSelector(state => state.meetingApproval.loading);

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
      <h2 id="meeting-approval-heading" data-cy="MeetingApprovalHeading">
        <Translate contentKey="meetingsApp.meetingApproval.home.title">Meeting Approvals</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="meetingsApp.meetingApproval.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/meeting-approval/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="meetingsApp.meetingApproval.home.createLabel">Create new Meeting Approval</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {meetingApprovalList && meetingApprovalList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="meetingsApp.meetingApproval.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('step')}>
                  <Translate contentKey="meetingsApp.meetingApproval.step">Step</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('step')} />
                </th>
                <th className="hand" onClick={sort('decision')}>
                  <Translate contentKey="meetingsApp.meetingApproval.decision">Decision</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('decision')} />
                </th>
                <th className="hand" onClick={sort('reason')}>
                  <Translate contentKey="meetingsApp.meetingApproval.reason">Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reason')} />
                </th>
                <th className="hand" onClick={sort('decidedAt')}>
                  <Translate contentKey="meetingsApp.meetingApproval.decidedAt">Decided At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('decidedAt')} />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingApproval.decidedBy">Decided By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingApproval.meeting">Meeting</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {meetingApprovalList.map((meetingApproval, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/meeting-approval/${meetingApproval.id}`} color="link" size="sm">
                      {meetingApproval.id}
                    </Button>
                  </td>
                  <td>{meetingApproval.step}</td>
                  <td>
                    <Translate contentKey={`meetingsApp.ApprovalDecision.${meetingApproval.decision}`} />
                  </td>
                  <td>{meetingApproval.reason}</td>
                  <td>
                    {meetingApproval.decidedAt ? (
                      <TextFormat type="date" value={meetingApproval.decidedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{meetingApproval.decidedBy ? meetingApproval.decidedBy.login : ''}</td>
                  <td>
                    {meetingApproval.meeting ? <Link to={`/meeting/${meetingApproval.meeting.id}`}>{meetingApproval.meeting.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/meeting-approval/${meetingApproval.id}`}
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
                        to={`/meeting-approval/${meetingApproval.id}/edit`}
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
                        onClick={() => (window.location.href = `/meeting-approval/${meetingApproval.id}/delete`)}
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
              <Translate contentKey="meetingsApp.meetingApproval.home.notFound">No Meeting Approvals found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default MeetingApproval;
