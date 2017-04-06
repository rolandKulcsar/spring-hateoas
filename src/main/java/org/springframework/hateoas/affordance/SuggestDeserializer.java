/*
 * Copyright 2016 the original author or authors.
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
package org.springframework.hateoas.affordance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class SuggestDeserializer extends JsonDeserializer<Suggestions> {

	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Suggestions deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonDeserializer<Object> deser = ctxt.findRootValueDeserializer(ctxt.constructType(Object.class));

		String textField = null;
		String valueField = null;
		String embeddedRel = null;
		String href = null;
		List<Object> list = new ArrayList<Object>();

		while (!JsonToken.END_OBJECT.equals(jp.nextToken())) {

			if ("values".equals(jp.getCurrentName())) {

				jp.nextToken();

				while (!JsonToken.END_ARRAY.equals(jp.nextToken())) {
					list.add(deser.deserialize(jp, ctxt));
				}

			} else if ("embedded".equals(jp.getCurrentName())) {
				embeddedRel = jp.getText();
			} else if ("href".equals(jp.getCurrentName())) {
				href = jp.getText();
			} else if ("prompt-field".equals(jp.getCurrentName())) {
				textField = jp.getText();
			} else if ("value-field".equals(jp.getCurrentName())) {
				valueField = jp.getText();
			}
		}

		if (valueField != null) {
			return Suggestions.values(list).withPromptField(textField).withValueField(valueField);
		} else if (href != null) {
			return Suggestions.remote(href).withPromptField(textField).withValueField(valueField);
		} else if (embeddedRel != null) {
			return Suggestions.external(embeddedRel).withPromptField(textField).withValueField(valueField);
		}

		return Suggestions.NONE;
	}
}
