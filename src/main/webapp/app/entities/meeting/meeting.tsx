import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './meeting.reducer';

export const Meeting = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const meetingList = useAppSelector(state => state.meeting.entities);
  const loading = useAppSelector(state => state.meeting.loading);
  const totalItems = useAppSelector(state => state.meeting.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="meeting-heading" data-cy="MeetingHeading">
        <Translate contentKey="meetingsApp.meeting.home.title">Meetings</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="meetingsApp.meeting.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/meeting/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="meetingsApp.meeting.home.createLabel">Create new Meeting</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {meetingList && meetingList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="meetingsApp.meeting.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="meetingsApp.meeting.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('startTime')}>
                  <Translate contentKey="meetingsApp.meeting.startTime">Start Time</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('startTime')} />
                </th>
                <th className="hand" onClick={sort('endTime')}>
                  <Translate contentKey="meetingsApp.meeting.endTime">End Time</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('endTime')} />
                </th>
                <th className="hand" onClick={sort('mode')}>
                  <Translate contentKey="meetingsApp.meeting.mode">Mode</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('mode')} />
                </th>
                <th className="hand" onClick={sort('onlineLink')}>
                  <Translate contentKey="meetingsApp.meeting.onlineLink">Online Link</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('onlineLink')} />
                </th>
                <th className="hand" onClick={sort('objectives')}>
                  <Translate contentKey="meetingsApp.meeting.objectives">Objectives</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('objectives')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  <Translate contentKey="meetingsApp.meeting.note">Note</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="meetingsApp.meeting.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="meetingsApp.meeting.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('submittedAt')}>
                  <Translate contentKey="meetingsApp.meeting.submittedAt">Submitted At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submittedAt')} />
                </th>
                <th className="hand" onClick={sort('approvedAt')}>
                  <Translate contentKey="meetingsApp.meeting.approvedAt">Approved At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('approvedAt')} />
                </th>
                <th className="hand" onClick={sort('canceledAt')}>
                  <Translate contentKey="meetingsApp.meeting.canceledAt">Canceled At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('canceledAt')} />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meeting.type">Type</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meeting.level">Level</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meeting.organizerDepartment">Organizer Department</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meeting.room">Room</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meeting.requester">Requester</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meeting.host">Host</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {meetingList.map((meeting, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/meeting/${meeting.id}`} color="link" size="sm">
                      {meeting.id}
                    </Button>
                  </td>
                  <td>{meeting.title}</td>
                  <td>{meeting.startTime ? <TextFormat type="date" value={meeting.startTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{meeting.endTime ? <TextFormat type="date" value={meeting.endTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    <Translate contentKey={`meetingsApp.MeetingMode.${meeting.mode}`} />
                  </td>
                  <td>{meeting.onlineLink}</td>
                  <td>{meeting.objectives}</td>
                  <td>{meeting.note}</td>
                  <td>
                    <Translate contentKey={`meetingsApp.MeetingStatus.${meeting.status}`} />
                  </td>
                  <td>{meeting.createdAt ? <TextFormat type="date" value={meeting.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{meeting.submittedAt ? <TextFormat type="date" value={meeting.submittedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{meeting.approvedAt ? <TextFormat type="date" value={meeting.approvedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{meeting.canceledAt ? <TextFormat type="date" value={meeting.canceledAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{meeting.type ? <Link to={`/meeting-type/${meeting.type.id}`}>{meeting.type.name}</Link> : ''}</td>
                  <td>{meeting.level ? <Link to={`/meeting-level/${meeting.level.id}`}>{meeting.level.name}</Link> : ''}</td>
                  <td>
                    {meeting.organizerDepartment ? (
                      <Link to={`/department/${meeting.organizerDepartment.id}`}>{meeting.organizerDepartment.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{meeting.room ? <Link to={`/room/${meeting.room.id}`}>{meeting.room.name}</Link> : ''}</td>
                  <td>{meeting.requester ? meeting.requester.login : ''}</td>
                  <td>{meeting.host ? meeting.host.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/meeting/${meeting.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/meeting/${meeting.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                        onClick={() =>
                          (window.location.href = `/meeting/${meeting.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
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
              <Translate contentKey="meetingsApp.meeting.home.notFound">No Meetings found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={meetingList && meetingList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Meeting;
