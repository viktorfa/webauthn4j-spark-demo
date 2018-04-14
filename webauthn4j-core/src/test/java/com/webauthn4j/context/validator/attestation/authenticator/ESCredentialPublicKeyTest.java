/*
 * Copyright 2002-2018 the original author or authors.
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

package com.webauthn4j.context.validator.attestation.authenticator;

import com.webauthn4j.attestation.authenticator.ESCredentialPublicKey;
import com.webauthn4j.test.CoreTestUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for ESCredentialPublicKey
 */
public class ESCredentialPublicKeyTest {

    @Test
    public void equals_test() {
        ESCredentialPublicKey instanceA = CoreTestUtil.createESCredentialPublicKey();
        ESCredentialPublicKey instanceB = CoreTestUtil.createESCredentialPublicKey();
        assertThat(instanceA).isEqualTo(instanceB);
    }
}