/*
 * Copyright (c) 2001-2007 Sun Microsystems, Inc.  All rights reserved.
 *
 *  The Sun Project JXTA(TM) Software License
 *
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *
 *  3. The end-user documentation included with the redistribution, if any, must 
 *     include the following acknowledgment: "This product includes software 
 *     developed by Sun Microsystems, Inc. for JXTA(TM) technology." 
 *     Alternately, this acknowledgment may appear in the software itself, if 
 *     and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must 
 *     not be used to endorse or promote products derived from this software 
 *     without prior written permission. For written permission, please contact 
 *     Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA", nor may 
 *     "JXTA" appear in their name, without prior written permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL SUN 
 *  MICROSYSTEMS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  JXTA is a registered trademark of Sun Microsystems, Inc. in the United 
 *  States and other countries.
 *
 *  Please see the license information page at :
 *  <http://www.jxta.org/project/www/license.html> for instructions on use of 
 *  the license in source files.
 *
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many individuals 
 *  on behalf of Project JXTA. For more information on Project JXTA, please see 
 *  http://www.jxta.org.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation. 
 */

package net.jxta.membership;

import net.jxta.credential.AuthenticationCredential;

/**
 * An Authenticator is returned by the
 * {@link MembershipService#apply(AuthenticationCredential) apply()} 
 * method of the Membership Service of a peergroup. When the authenticator has 
 * been completed it is returned to the Membership Service via the 
 * {@link MembershipService#join(Authenticator) join()}
 * operation.
 *
 * <p/>The mechanism for completing authentication is unique for each
 * authentication method. (That's the whole point of writing a Membership
 * Service/Authentication). The only common operation is 
 * {@code isReadyForJoin()}, which provides confirmation as to whether you 
 * have completed the authenticator correctly.
 *
 * @see net.jxta.membership.MembershipService
 * @see net.jxta.credential.Credential
 * @see net.jxta.credential.AuthenticationCredential
 */
public interface Authenticator {

    /**
     *  Returns the name of this authentication method. This should be the same
     *  name which was used in the Authentication credential.
     *
     *  @return String containing the name of this authentication method.
     **/
    public String getMethodName();

    /**
     * Return the Authentication Credential associated with this authenticator,
     * if any.
     *
     * @return the AutheticationCredential which was provided to the
     * {@link MembershipService#apply(AuthenticationCredential)}.
     **/
    public AuthenticationCredential getAuthenticationCredential();

    /**
     * Returns the service which generated this authenticator. This is the
     * service which provided this authenticator and the service which will
     * accept this authenticator when the authenticator is
     * completed.
     *
     * @return the MembershipService service associated with this authenticator.
     **/
    public MembershipService  getSourceService();

    /**
     * Returns {@code true} if the minimal requirements of this Authenticator 
     * have been satisfied and it is  ready for submission to 
     * {@link MembershipService#join(Authenticator)}.
     * <p/>
     * Membership services may use this method in a variety of ways. The 
     * simplest (and most common) usage is to ensure that all of the required
     * authentication parameters have acceptable (not necessarily correct) 
     * values. Some authenticators may behave asynchronously and this method can 
     * be used to determine if the authentication process has completed.
     * <p/>
     * In all cases {@link #isReadyForJoin()} should be lower cost than calling
     * {@link MembershipService#join(Authenticator)}.
     * <p/>
     * This method provides no distinction between incomplete authentication
     * and failed authentication.
     *
     * @see MembershipService#join(Authenticator)
     *
     * @return {@code true} if the authenticator is "complete" and ready for
     * submitting to {@link MembershipService#join(Authenticator)}, otherwise 
     * {@code false}.
     **/
    public boolean  isReadyForJoin();
}

