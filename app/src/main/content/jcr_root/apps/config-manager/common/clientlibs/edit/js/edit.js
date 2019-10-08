/*
 *  Copyright 2019 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function (document, $) {
    'use strict';

    var editMode = false;

    var CLOUDCONFIGS_PATH = 'settings/cloudconfigs';
    var configType = "adobe-fonts";
    var configAdmin = "";
    var contentPath = "";

    var $form;

    var editConfigurationForm = "#edit-configuration-properties-form";

    $(document).on('foundation-contentloaded', function () {
        $form = $('#edit-configuration-properties-form');
        configType = $form.data('configType');
        configAdmin = $form.data('configAdmin');
        contentPath = $form.data('contentPath');

        if (contentPath.indexOf(CLOUDCONFIGS_PATH) < 0) {
            contentPath = contentPath + '/' + CLOUDCONFIGS_PATH + '/' + configType;
        }

        $form.on('submit', doEditConfiguration);
    });

    function doEditConfiguration(event) {
        event.preventDefault();
        event.stopImmediatePropagation();

        var path = $form.data("post-url-edit");

        var editData = $form.serialize();

        editData = editData.split('.%2F').join('jcr%3Acontent%2F');

        var postDataPrefix = "jcr:primaryType=cq:Page";

        var postData = encodeURI(postDataPrefix) + "&" + editData;

        console.log(postData);

        $.ajax({
            type: 'post',
            url: path,
            data: postData,
            enctype: 'multipart/form-data',
            cache: false
        }).done(function (data, result) {
            location.href = configAdmin;
        }).fail(function (jqXHR, textStatus, errorThrown) {
            var ui = $(window).adaptTo("foundation-ui");
            ui.notify(null, Granite.I18n.get(jqXHR.responseText), 'error');
        });
    }
})(document, Granite.$);
