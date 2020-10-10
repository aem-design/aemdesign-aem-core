/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 AEM.Design
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.utils;

import com.adobe.granite.asset.api.Rendition;

import java.util.Comparator;

import static design.aem.utils.components.ImagesUtil.getWidth;

/***
 * allow asset rendition compare by width
 */
public class WidthBasedRenditionComparator implements Comparator<Rendition> {
    public int compare(com.adobe.granite.asset.api.Rendition r1, com.adobe.granite.asset.api.Rendition r2) {
        int w1 = getWidth(r1);
        int w2 = getWidth(r2);
        if (w1 < w2) {
            return -1;
        }
        if (w1 == w2) {
            return 0;
        }
        return 1;
    }
}
