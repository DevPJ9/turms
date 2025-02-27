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
// source: request/user/query_nearby_users_request.proto

package im.turms.server.common.access.client.dto.request.user;

public interface QueryNearbyUsersRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:im.turms.proto.QueryNearbyUsersRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>float latitude = 1;</code>
   * @return The latitude.
   */
  float getLatitude();

  /**
   * <code>float longitude = 2;</code>
   * @return The longitude.
   */
  float getLongitude();

  /**
   * <code>optional int32 distance = 3;</code>
   * @return Whether the distance field is set.
   */
  boolean hasDistance();
  /**
   * <code>optional int32 distance = 3;</code>
   * @return The distance.
   */
  int getDistance();

  /**
   * <code>optional int32 max_number = 4;</code>
   * @return Whether the maxNumber field is set.
   */
  boolean hasMaxNumber();
  /**
   * <code>optional int32 max_number = 4;</code>
   * @return The maxNumber.
   */
  int getMaxNumber();

  /**
   * <code>optional bool with_coordinates = 5;</code>
   * @return Whether the withCoordinates field is set.
   */
  boolean hasWithCoordinates();
  /**
   * <code>optional bool with_coordinates = 5;</code>
   * @return The withCoordinates.
   */
  boolean getWithCoordinates();

  /**
   * <code>optional bool with_distance = 6;</code>
   * @return Whether the withDistance field is set.
   */
  boolean hasWithDistance();
  /**
   * <code>optional bool with_distance = 6;</code>
   * @return The withDistance.
   */
  boolean getWithDistance();

  /**
   * <code>optional bool with_info = 7;</code>
   * @return Whether the withInfo field is set.
   */
  boolean hasWithInfo();
  /**
   * <code>optional bool with_info = 7;</code>
   * @return The withInfo.
   */
  boolean getWithInfo();
}
