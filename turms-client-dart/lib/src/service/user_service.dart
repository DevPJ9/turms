import 'package:fixnum/fixnum.dart';

import '../../turms_client.dart';
import '../extension/int_extensions.dart';
import '../util/system.dart';

class Location {
  final double longitude;
  final double latitude;

  Location(this.longitude, this.latitude);
}

class UserInfo {
  Int64 userId;
  String? password;
  DeviceType? deviceType;
  Map<String, String>? deviceDetails;
  UserStatus? onlineStatus;
  Location? location;

  UserInfo(this.userId, this.password, this.deviceType, this.deviceDetails,
      this.onlineStatus, this.location);
}

typedef OnOnlineListener = void Function();
typedef OnOfflineListener = void Function(SessionCloseInfo info);

class UserService {
  final TurmsClient _turmsClient;
  UserInfo? _userInfo;
  bool _storePassword = false;

  final List<OnOnlineListener> _onOnlineListeners = [];
  final List<OnOfflineListener> _onOfflineListeners = [];

  UserService(this._turmsClient) {
    _turmsClient.driver
      ..addOnDisconnectedListener((_) => _changeToOffline(
          SessionCloseInfo.fromCloseStatus(
              SessionCloseStatus.connectionClosed)))
      ..addNotificationListener((notification) {
        if (notification.hasCloseStatus() && isLoggedIn) {
          _changeToOffline(SessionCloseInfo(notification.closeStatus,
              notification.code, notification.reason));
        }
      });
  }

  UserInfo? get userInfo => _userInfo;

  bool get isLoggedIn =>
      _userInfo != null && _userInfo?.onlineStatus != UserStatus.OFFLINE;

  void addOnOnlineListener(OnOnlineListener listener) =>
      _onOnlineListeners.add(listener);

  void addOnOfflineListener(OnOfflineListener listener) =>
      _onOfflineListeners.add(listener);

  void removeOnOnlineListener(OnOnlineListener listener) =>
      _onOnlineListeners.remove(listener);

  void removeOnOfflineListener(OnOfflineListener listener) =>
      _onOfflineListeners.remove(listener);

  Future<void> login(Int64 userId,
      {String? password,
      DeviceType? deviceType,
      Map<String, String>? deviceDetails,
      UserStatus? onlineStatus,
      Location? location,
      bool storePassword = false}) async {
    final user = UserInfo(userId, storePassword ? password : null,
        deviceType ?? currentDeviceType, deviceDetails, onlineStatus, location);
    if (!_turmsClient.driver.isConnected) {
      await _turmsClient.driver.connect();
    }
    await _turmsClient.driver.send(CreateSessionRequest(
        version: 1,
        userId: userId,
        password: password,
        deviceType: deviceType,
        deviceDetails: deviceDetails,
        userStatus: onlineStatus,
        location: location == null
            ? null
            : UserLocation(
                longitude: location.longitude, latitude: location.latitude)));
    _changeToOnline();
    _storePassword = storePassword;
    _userInfo = user;
  }

  Future<void> logout({bool disconnect = true}) async {
    if (disconnect) {
      await _turmsClient.driver.disconnect();
    } else {
      await _turmsClient.driver.send(DeleteSessionRequest());
    }
    _changeToOffline(SessionCloseInfo.fromCloseStatus(
        SessionCloseStatus.disconnectedByClient));
  }

  Future<void> updateOnlineStatus(UserStatus onlineStatus) async {
    await _turmsClient.driver
        .send(UpdateUserOnlineStatusRequest(userStatus: onlineStatus));
    _userInfo?.onlineStatus = onlineStatus;
  }

  Future<void> disconnectOnlineDevices(List<DeviceType> deviceTypes) async {
    if (deviceTypes.isEmpty) {
      throw ResponseException(
          ResponseStatusCode.illegalArgument, 'deviceTypes must not be empty');
    }
    await _turmsClient.driver.send(UpdateUserOnlineStatusRequest(
        userStatus: UserStatus.OFFLINE, deviceTypes: deviceTypes));
  }

  Future<void> updatePassword(String password) async {
    await _turmsClient.driver.send(UpdateUserRequest(password: password));
    if (_storePassword) {
      _userInfo?.password = password;
    }
  }

  Future<void> updateProfile(
      {String? name,
      String? intro,
      ProfileAccessStrategy? profileAccessStrategy}) async {
    if ([name, intro, profileAccessStrategy].areAllNull) {
      return;
    }
    await _turmsClient.driver.send(UpdateUserRequest(
        name: name,
        intro: intro,
        profileAccessStrategy: profileAccessStrategy));
  }

  Future<UserInfoWithVersion?> queryUserProfile(Int64 userId,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryUserProfileRequest(
        userId: userId, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    if (!n.data.hasUsersInfosWithVersion()) {
      return null;
    }
    final usersInfosWithVersion = n.data.usersInfosWithVersion;
    final date = usersInfosWithVersion.hasLastUpdatedDate()
        ? usersInfosWithVersion.lastUpdatedDate.toDateTime()
        : null;
    return UserInfoWithVersion(usersInfosWithVersion.userInfos[0], date);
  }

  Future<List<NearbyUser>> queryNearbyUsers(double latitude, double longitude,
      {int? distance,
      int? maxNumber,
      bool? withCoordinates,
      bool? withDistance,
      bool? withInfo}) async {
    final n = await _turmsClient.driver.send(QueryNearbyUsersRequest(
        latitude: latitude,
        longitude: longitude,
        distance: distance,
        maxNumber: maxNumber,
        withCoordinates: withCoordinates,
        withDistance: withDistance,
        withInfo: withInfo));
    return n.data.nearbyUsers.nearbyUsers;
  }

  Future<List<UserStatusDetail>> queryOnlineStatusesRequest(
      Set<Int64> userIds) async {
    final n = await _turmsClient.driver
        .send(QueryUserOnlineStatusesRequest(userIds: userIds));
    return n.data.usersOnlineStatuses.userStatuses;
  }

  // Relationship

  Future<UserRelationshipsWithVersion?> queryRelationships(
      {Set<Int64>? relatedUserIds,
      bool? isBlocked,
      int? groupIndex,
      DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryRelationshipsRequest(
        userIds: relatedUserIds,
        blocked: isBlocked,
        groupIndex: groupIndex,
        lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasUserRelationshipsWithVersion()
        ? n.data.userRelationshipsWithVersion
        : null;
  }

  Future<Int64ValuesWithVersion?> queryRelatedUserIds(
      {bool? isBlocked, int? groupIndex, DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryRelatedUserIdsRequest(
        blocked: isBlocked,
        groupIndex: groupIndex,
        lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasIdsWithVersion() ? n.data.idsWithVersion : null;
  }

  Future<UserRelationshipsWithVersion?> queryFriends(
          {int? groupIndex, DateTime? lastUpdatedDate}) =>
      queryRelationships(
          isBlocked: false,
          groupIndex: groupIndex,
          lastUpdatedDate: lastUpdatedDate);

  Future<UserRelationshipsWithVersion?> queryBlockedUsers(
          {int? groupIndex, DateTime? lastUpdatedDate}) =>
      queryRelationships(
          isBlocked: true,
          groupIndex: groupIndex,
          lastUpdatedDate: lastUpdatedDate);

  Future<void> createRelationship(Int64 userId, bool isBlocked,
      {int? groupIndex}) async {
    await _turmsClient.driver.send(CreateRelationshipRequest(
        userId: userId, blocked: isBlocked, groupIndex: groupIndex));
  }

  Future<void> createFriendRelationship(Int64 userId, {int? groupIndex}) =>
      createRelationship(userId, false, groupIndex: groupIndex);

  Future<void> createBlockedUserRelationship(Int64 userId, {int? groupIndex}) =>
      createRelationship(userId, true, groupIndex: groupIndex);

  Future<void> deleteRelationship(Int64 relatedUserId,
      {int? deleteGroupIndex, int? targetGroupIndex}) async {
    await _turmsClient.driver.send(DeleteRelationshipRequest(
        userId: relatedUserId,
        groupIndex: deleteGroupIndex,
        targetGroupIndex: targetGroupIndex));
  }

  Future<void> updateRelationship(Int64 relatedUserId,
      {bool? isBlocked, int? groupIndex}) async {
    if ([isBlocked, groupIndex].areAllNull) {
      return;
    }
    await _turmsClient.driver.send(UpdateRelationshipRequest(
        userId: relatedUserId, blocked: isBlocked, newGroupIndex: groupIndex));
  }

  Future<Int64> sendFriendRequest(Int64 recipientId, String content) async {
    final n = await _turmsClient.driver.send(
        CreateFriendRequestRequest(recipientId: recipientId, content: content));
    return n.getFirstIdOrThrow();
  }

  Future<void> replyFriendRequest(
      Int64 requestId, ResponseAction responseAction,
      {String? reason}) async {
    await _turmsClient.driver.send(UpdateFriendRequestRequest(
        requestId: requestId, responseAction: responseAction, reason: reason));
  }

  Future<UserFriendRequestsWithVersion?> queryFriendRequests(bool areSentByMe,
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryFriendRequestsRequest(
        areSentByMe: areSentByMe, lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasUserFriendRequestsWithVersion()
        ? n.data.userFriendRequestsWithVersion
        : null;
  }

  Future<Int64> createRelationshipGroup(String name) async {
    final n = await _turmsClient.driver
        .send(CreateRelationshipGroupRequest(name: name));
    return n.getFirstIdOrThrow();
  }

  Future<void> deleteRelationshipGroups(int groupIndex,
      {int? targetGroupIndex}) async {
    await _turmsClient.driver.send(DeleteRelationshipGroupRequest(
        groupIndex: groupIndex, targetGroupIndex: targetGroupIndex));
  }

  Future<void> updateRelationshipGroup(int groupIndex, String newName) async {
    await _turmsClient.driver.send(UpdateRelationshipGroupRequest(
        groupIndex: groupIndex, newName: newName));
  }

  Future<UserRelationshipGroupsWithVersion?> queryRelationshipGroups(
      {DateTime? lastUpdatedDate}) async {
    final n = await _turmsClient.driver.send(QueryRelationshipGroupsRequest(
        lastUpdatedDate: lastUpdatedDate?.toInt64()));
    return n.data.hasUserRelationshipGroupsWithVersion()
        ? n.data.userRelationshipGroupsWithVersion
        : null;
  }

  Future<void> moveRelatedUserToGroup(
      Int64 relatedUserId, int groupIndex) async {
    await _turmsClient.driver.send(UpdateRelationshipRequest(
        userId: relatedUserId, newGroupIndex: groupIndex));
  }

  /// updateLocation() in UserService is different from sendMessage()
  /// with records of location in MessageService
  /// updateLocation() in UserService sends the location of user to
  /// the server only.
  /// sendMessage() with records of location sends user's location to
  /// both server and its recipients.
  Future<void> updateLocation(double latitude, double longitude,
      {String? name, String? address}) async {
    await _turmsClient.driver.send(UpdateUserLocationRequest(
        latitude: latitude,
        longitude: longitude,
        name: name,
        address: address));
  }

  void _changeToOnline() {
    if (!isLoggedIn) {
      _turmsClient.driver.stateStore.isSessionOpen = true;
      for (final listener in _onOnlineListeners) {
        listener.call();
      }
    }
  }

  void _changeToOffline(SessionCloseInfo sessionCloseInfo) {
    if (isLoggedIn) {
      _userInfo?.onlineStatus = UserStatus.OFFLINE;
      _turmsClient.driver.stateStore.isSessionOpen = false;
      for (final listener in _onOfflineListeners) {
        listener.call(sessionCloseInfo);
      }
    }
  }
}
