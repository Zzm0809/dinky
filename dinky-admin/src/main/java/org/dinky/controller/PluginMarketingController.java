/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.controller;

import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.enums.Status;
import org.dinky.data.model.PluginMarketing;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.PluginMarketingService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@Api(tags = "Plugin Marketing Controller")
@RequiredArgsConstructor
@RequestMapping("/api/plugin-marketing")
@SaCheckLogin
public class PluginMarketingController {

    private final PluginMarketingService pluginMarketService;

    @GetMapping("/sync")
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_SYNC)
    public Result<Void> syncPlugins() {
        boolean syncPluginMarketData = pluginMarketService.syncPluginMarketData();
        if (!syncPluginMarketData) {
            return Result.failed(Status.SYNC_FAILED);
        }
        return Result.succeed(Status.SYNC_SUCCESS);
    }

    @PostMapping("/list")
    @ApiOperation("Get Plugin List")
    @ApiImplicitParam(name = "params", value = "params", dataType = "JsonNode", paramType = "body", required = true)
    public ProTableResult<PluginMarketing> listToken(@RequestBody JsonNode params) {
        return pluginMarketService.selectForProTable(params);
    }

    @GetMapping("/query-all-version-by-plugin-id")
    @ApiOperation("Get Plugin List")
    @ApiImplicitParam(name = "params", value = "params", dataType = "JsonNode", paramType = "body", required = true)
    public Result<List<String>> queryAllVersionByPluginId(@RequestParam(required = true) Integer id) {
        List<String> pluginMarketings = pluginMarketService.queryAllVersionByPluginId(id);
        return Result.succeed(pluginMarketings);
    }

    @PostMapping("/download")
    @ApiOperation("Download Plugin")
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DOWNLOAD)
    public Result<Void> downloadPlugin(@RequestBody PluginMarketing pluginMarketing) {
        boolean downloadAndLoadDependency = pluginMarketService.downloadedPlugin(pluginMarketing);
        if (!downloadAndLoadDependency) {
            return Result.failed(Status.DOWNLOAD_FAILED);
        }
        return Result.succeed(Status.DELETE_SUCCESS);
    }

    @PostMapping("/install")
    @ApiOperation("Install Plugin")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_INSTALL)
    public Result<Void> installPlugin(@RequestBody PluginMarketing pluginMarketing) {
        boolean downloadAndLoadDependency = pluginMarketService.installPlugin(pluginMarketing);
        if (!downloadAndLoadDependency) {
            return Result.failed(Status.INSTALL_FAILED);
        }
        return Result.succeed(Status.INSTALL_SUCCESS);
    }

    @DeleteMapping("/uninstall")
    @ApiOperation("Uninstall Plugin")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_UNINSTALL)
    public Result<Void> uninstallPlugin(@RequestParam("id") Integer id) {
        boolean uninstalledPlugin = pluginMarketService.uninstallPlugin(id);
        if (!uninstalledPlugin) {
            return Result.failed(Status.UNINSTALL_FAILED);
        }
        return Result.succeed(Status.UNINSTALL_SUCCESS);
    }

    @DeleteMapping("/delete")
    @ApiOperation("Delete Plugin")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DELETE)
    public Result<Void> deletePlugin(@RequestParam("id") Integer id) {
        boolean uninstalledPlugin = pluginMarketService.deletePlugin(id);
        if (!uninstalledPlugin) {
            return Result.failed(Status.DELETE_FAILED);
        }
        return Result.succeed(Status.DELETE_SUCCESS);
    }
}
