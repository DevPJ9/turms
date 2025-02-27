/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: notification/turms_notification.proto

package im.turms.server.common.access.client.dto.notification;

public final class TurmsNotificationOuterClass {
  private TurmsNotificationOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_im_turms_proto_TurmsNotification_descriptor;
  static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_im_turms_proto_TurmsNotification_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_im_turms_proto_TurmsNotification_Data_descriptor;
  static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_im_turms_proto_TurmsNotification_Data_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n%notification/turms_notification.proto\022" +
      "\016im.turms.proto\032\033request/turms_request.p" +
      "roto\032\037model/common/int64_values.proto\032,m" +
      "odel/common/int64_values_with_version.pr" +
      "oto\0320model/group/group_invitations_with_" +
      "version.proto\0324model/group/group_join_qu" +
      "estions_answer_result.proto\0323model/group" +
      "/group_join_questions_with_version.proto" +
      "\0322model/group/group_join_requests_with_v" +
      "ersion.proto\032,model/group/group_members_" +
      "with_version.proto\032%model/group/groups_w" +
      "ith_version.proto\032&model/conversation/co" +
      "nversations.proto\032\034model/message/message" +
      "s.proto\032,model/message/messages_with_tot" +
      "al_list.proto\032\035model/user/nearby_users.p" +
      "roto\0322model/user/user_friend_requests_wi" +
      "th_version.proto\032(model/user/user_infos_" +
      "with_version.proto\032%model/user/user_onli" +
      "ne_statuses.proto\0326model/user/user_relat" +
      "ionship_groups_with_version.proto\0320model" +
      "/user/user_relationships_with_version.pr" +
      "oto\032\035model/user/user_session.proto\"\233\r\n\021T" +
      "urmsNotification\022\021\n\ttimestamp\030\001 \001(\003\022\027\n\nr" +
      "equest_id\030\004 \001(\003H\000\210\001\001\022\021\n\004code\030\005 \001(\005H\001\210\001\001\022" +
      "\023\n\006reason\030\006 \001(\tH\002\210\001\001\0224\n\004data\030\007 \001(\0132&.im." +
      "turms.proto.TurmsNotification.Data\022\031\n\014re" +
      "quester_id\030\n \001(\003H\003\210\001\001\022\031\n\014close_status\030\013 " +
      "\001(\005H\004\210\001\001\0225\n\017relayed_request\030\014 \001(\0132\034.im.t" +
      "urms.proto.TurmsRequest\032\311\n\n\004Data\022*\n\003ids\030" +
      "\001 \001(\0132\033.im.turms.proto.Int64ValuesH\000\022B\n\020" +
      "ids_with_version\030\002 \001(\0132&.im.turms.proto." +
      "Int64ValuesWithVersionH\000\022\r\n\003url\030\003 \001(\tH\000\022" +
      "6\n\rconversations\030\004 \001(\0132\035.im.turms.proto." +
      "ConversationsH\000\022,\n\010messages\030\005 \001(\0132\030.im.t" +
      "urms.proto.MessagesH\000\022I\n\030messages_with_t" +
      "otal_list\030\006 \001(\0132%.im.turms.proto.Message" +
      "sWithTotalListH\000\0223\n\014user_session\030\007 \001(\0132\033" +
      ".im.turms.proto.UserSessionH\000\022G\n\027user_in" +
      "fos_with_version\030\010 \001(\0132$.im.turms.proto." +
      "UserInfosWithVersionH\000\022B\n\024user_online_st" +
      "atuses\030\t \001(\0132\".im.turms.proto.UserOnline" +
      "StatusesH\000\022Z\n!user_friend_requests_with_" +
      "version\030\n \001(\0132-.im.turms.proto.UserFrien" +
      "dRequestsWithVersionH\000\022b\n%user_relations" +
      "hip_groups_with_version\030\013 \001(\01321.im.turms" +
      ".proto.UserRelationshipGroupsWithVersion" +
      "H\000\022W\n\037user_relationships_with_version\030\014 " +
      "\001(\0132,.im.turms.proto.UserRelationshipsWi" +
      "thVersionH\000\0223\n\014nearby_users\030\r \001(\0132\033.im.t" +
      "urms.proto.NearbyUsersH\000\022U\n\036group_invita" +
      "tions_with_version\030\016 \001(\0132+.im.turms.prot" +
      "o.GroupInvitationsWithVersionH\000\022[\n!group" +
      "_join_question_answer_result\030\017 \001(\0132..im." +
      "turms.proto.GroupJoinQuestionsAnswerResu" +
      "ltH\000\022X\n group_join_requests_with_version" +
      "\030\020 \001(\0132,.im.turms.proto.GroupJoinRequest" +
      "sWithVersionH\000\022Z\n!group_join_questions_w" +
      "ith_version\030\021 \001(\0132-.im.turms.proto.Group" +
      "JoinQuestionsWithVersionH\000\022M\n\032group_memb" +
      "ers_with_version\030\022 \001(\0132\'.im.turms.proto." +
      "GroupMembersWithVersionH\000\022@\n\023groups_with" +
      "_version\030\023 \001(\0132!.im.turms.proto.GroupsWi" +
      "thVersionH\000B\006\n\004kindB\r\n\013_request_idB\007\n\005_c" +
      "odeB\t\n\007_reasonB\017\n\r_requester_idB\017\n\r_clos" +
      "e_statusB<\n5im.turms.server.common.acces" +
      "s.client.dto.notificationP\001\272\002\000b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          im.turms.server.common.access.client.dto.request.TurmsRequestOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.common.Int64ValuesOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.common.Int64ValuesWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.group.GroupInvitationsWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.group.GroupJoinQuestionsAnswerResultOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.group.GroupJoinQuestionsWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.group.GroupJoinRequestsWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.group.GroupMembersWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.group.GroupsWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.conversation.ConversationsOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.message.MessagesOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.message.MessagesWithTotalListOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.user.NearbyUsersOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.user.UserFriendRequestsWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.user.UserInfosWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.user.UserOnlineStatusesOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.user.UserRelationshipGroupsWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.user.UserRelationshipsWithVersionOuterClass.getDescriptor(),
          im.turms.server.common.access.client.dto.model.user.UserSessionOuterClass.getDescriptor(),
        });
    internal_static_im_turms_proto_TurmsNotification_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_im_turms_proto_TurmsNotification_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_im_turms_proto_TurmsNotification_descriptor,
        new java.lang.String[] { "Timestamp", "RequestId", "Code", "Reason", "Data", "RequesterId", "CloseStatus", "RelayedRequest", "RequestId", "Code", "Reason", "RequesterId", "CloseStatus", });
    internal_static_im_turms_proto_TurmsNotification_Data_descriptor =
      internal_static_im_turms_proto_TurmsNotification_descriptor.getNestedTypes().get(0);
    internal_static_im_turms_proto_TurmsNotification_Data_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_im_turms_proto_TurmsNotification_Data_descriptor,
        new java.lang.String[] { "Ids", "IdsWithVersion", "Url", "Conversations", "Messages", "MessagesWithTotalList", "UserSession", "UserInfosWithVersion", "UserOnlineStatuses", "UserFriendRequestsWithVersion", "UserRelationshipGroupsWithVersion", "UserRelationshipsWithVersion", "NearbyUsers", "GroupInvitationsWithVersion", "GroupJoinQuestionAnswerResult", "GroupJoinRequestsWithVersion", "GroupJoinQuestionsWithVersion", "GroupMembersWithVersion", "GroupsWithVersion", "Kind", });
    im.turms.server.common.access.client.dto.request.TurmsRequestOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.common.Int64ValuesOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.common.Int64ValuesWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.group.GroupInvitationsWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.group.GroupJoinQuestionsAnswerResultOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.group.GroupJoinQuestionsWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.group.GroupJoinRequestsWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.group.GroupMembersWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.group.GroupsWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.conversation.ConversationsOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.message.MessagesOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.message.MessagesWithTotalListOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.user.NearbyUsersOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.user.UserFriendRequestsWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.user.UserInfosWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.user.UserOnlineStatusesOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.user.UserRelationshipGroupsWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.user.UserRelationshipsWithVersionOuterClass.getDescriptor();
    im.turms.server.common.access.client.dto.model.user.UserSessionOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
