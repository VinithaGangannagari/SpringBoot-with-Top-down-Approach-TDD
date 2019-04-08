package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;Petri KainulainenHOMEABOUT MEBLOGTUTORIALSCOURSES
        Are you tired of writing tests which have a lot of boilerplate code? If so, get started with Spock Framework >>

        Writing Clean Tests – To Verify Or Not To Verify
        Petri Kainulainen August 3, 2014 14 comments
        Clean Code, Clean Tests, Integration Testing, Unit Testing

        When we write unit tests that use mock objects, we follow these steps:

        Configure the behavior of our mock objects.
        Invoke the tested method.
        Verify that the correct methods of our mock objects were invoked.
        The description of the third step is actually a bit misleading, because often we end up verifying that the correct methods were invoked AND that the other methods of our mock objects were not invoked.

        And everyone knows that if we want to write bug free software, we have to verify both of these things or bad things happen.

        Right?

        My "Test With Spring" course helps you to write unit, integration, and end-to-end tests for Spring and Spring Boot Web Apps:
        CHECK IT OUT >>

        Let’s Verify Everything
        Let’s start by taking a look at the implementation of a service method that is used to add new user accounts to the database.

        The requirements of this service method are:

        If the email address of the registered user account is not unique, our service method must throw an exception.
        If the registered user account has a unique email address, our service method must add a new user account to the database.
        If the registered user account has a unique email address and it is created by using normal sign in, our service method must encode the user’s password before it is saved to the database.
        If the registered user account has a unique email address and it is created by using social sign in, our service method must save the used social sign in provider.
        A user account that was created by using social sign in must not have a password.
        Our service method must return the information of the created user account.
        If you want learn how you can specify the requirements of a service method, you should read the following blog posts:
        From Top to Bottom: TDD for Web Applications
        From Idea to Code: The Lifecycle of Agile Specifications
        This service method is implemented by following these steps:

        The service method checks that the email address given by user is not found from the database. It does this by invoking the findByEmail() method of the UserRepository interface.
        If the User object is found, the service method method throws a DuplicateEmailException.
        It creates a new User object. If the registration is made by using a normal sign in (the signInProvider property of the RegistrationForm class is not set), the service method encodes the password provided by user, and sets the encoded password to the created User object.
        The service methods saves the information of the created User object to the database and returns the saved User object.
        The source code of the RepositoryUserService class looks as follows:

        1
        2
        3
        4
        5
        6
        7
        8
        9
        10
        11
        12
        13
        14
        15
        16
        17
        18
        19
        20
        21
        22
        23
        24
        25
        26
        27
        28
        29
        30
        31
        32
        33
        34
        35
        36
        37
        38
        39
        40
        41
        42
        43
        44
        45
        46
        47
        48
        49
        50
        51
        52
        53
        54
        55
        56
        57
        58
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

@Service
public class RepositoryUserService implements UserService {

    private PasswordEncoder passwordEncoder;

    private UserRepository repository;

    @Autowired
    public RepositoryUserService(PasswordEncoder passwordEncoder, UserRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Transactional
    @Override
    public User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException {
        if (emailExist(userAccountData.getEmail())) {
            throw new DuplicateEmailException("The email address: " + userAccountData.getEmail() + " is already in use.");
        }

        String encodedPassword = encodePassword(userAccountData);

        User registered = User.getBuilder()
                .email(userAccountData.getEmail())
                .firstName(userAccountData.getFirstName())
                .lastName(userAccountData.getLastName())
                .password(encodedPassword)
                .signInProvider(userAccountData.getSignInProvider())
                .build();

        return repository.save(registered);
    }

    private boolean emailExist(String email) {
        User user = repository.findByEmail(email);

        if (user != null) {
            return true;
        }

        return false;
    }

    private String encodePassword(RegistrationForm dto) {
        String encodedPassword = null;

        if (dto.isNormalRegistration()) {
            encodedPassword = passwordEncoder.encode(dto.getPassword());
        }

        return encodedPassword;
    }
}
    If we want write unit tests which ensure that our service method is working correctly when the user is registering a new user account by using social sign in AND we want to verify every interaction between our service method and our mock objects, we have to write eight unit tests for it.

        We have to ensure that:

        The service methods checks that email address is unique when a duplicate email address is given.
        The DuplicateEmailException is thrown when a duplicate email address is given.
        The service method doesn’t save a new account to the database when a duplicate email address is given.
        Our service method doesn’t encode the user’s password if a duplicate email address is given.
        Our service method checks that the email address is unique when a unique email address is given.
        When a unique email address is given, our service method creates a new User object that contains the correct information and saves the information of the created User object to the database.
        When a unique email address is given, our service method returns the information of the created user account.
        When a unique email address is given and a social sign in is used, our service method must not set the password of the created user account (or encode it).
        The source code of our test class looks as follows:

        1
        2
        3
        4
        5
        6
        7
        8
        9
        10
        11
        12
        13
        14
        15
        16
        17
        18
        19
        20
        21
        22
        23
        24
        25
        26
        27
        28
        29
        30
        31
        32
        33
        34
        35
        36
        37
        38
        39
        40
        41
        42
        43
        44
        45
        46
        47
        48
        49
        50
        51
        52
        53
        54
        55
        56
        57
        58
        59
        60
        61
        62
        63
        64
        65
        66
        67
        68
        69
        70
        71
        72
        73
        74
        75
        76
        77
        78
        79
        80
        81
        82
        83
        84
        85
        86
        87
        88
        89
        90
        91
        92
        93
        94
        95
        96
        97
        98
        99
        100
        101
        102
        103
        104
        105
        106
        107
        108
        109
        110
        111
        112
        113
        114
        115
        116
        117
        118
        119
        120
        121
        122
        123
        124
        125
        126
        127
        128
        129
        130
        131
        132
        133
        134
        135
        136
        137
        138
        139
        140
        141
        142
        143
        144
        145
        146
        147
        148
        149
        150
        151
        152
        153
        154
        155
        156
        157
        158
        159
        160
        161
        162
        163
        164
        165
        166
        167
        168
        169
        170
        171
        172
        173
        174
        175
        176
        177
        178
        179
        180
        181
        182
        183
        184
        185
        186
        187
        188
        189
        190
        191
        192
        193
        194
        195
        196
        197
        198
        199
        import net.petrikainulainen.spring.social.signinmvc.user.dto.RegistrationForm;
        import net.petrikainulainen.spring.social.signinmvc.user.dto.RegistrationFormBuilder;
        import net.petrikainulainen.spring.social.signinmvc.user.model.SocialMediaService;
        import net.petrikainulainen.spring.social.signinmvc.user.model.User;
        import net.petrikainulainen.spring.social.signinmvc.user.repository.UserRepository;
        import org.junit.Before;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.mockito.ArgumentCaptor;
        import org.mockito.Mock;
        import org.mockito.invocation.InvocationOnMock;
        import org.mockito.runners.MockitoJUnitRunner;
        import org.mockito.stubbing.Answer;
        import org.springframework.security.crypto.password.PasswordEncoder;

        import static com.googlecode.catchexception.CatchException.catchException;
        import static com.googlecode.catchexception.CatchException.caughtException;
        import static net.petrikainulainen.spring.social.signinmvc.user.model.UserAssert.assertThatUser;
        import static org.assertj.core.api.Assertions.assertThat;
        import static org.mockito.Matchers.isA;
        import static org.mockito.Mockito.never;
        import static org.mockito.Mockito.times;
        import static org.mockito.Mockito.verify;
        import static org.mockito.Mockito.verifyZeroInteractions;
        import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserServiceTest {

    private static final String REGISTRATION_EMAIL_ADDRESS = "john.smith@gmail.com";
    private static final String REGISTRATION_FIRST_NAME = "John";
    private static final String REGISTRATION_LAST_NAME = "Smith";
    private static final SocialMediaService SOCIAL_SIGN_IN_PROVIDER = SocialMediaService.TWITTER;

    private RepositoryUserService registrationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository repository;

    @Before
    public void setUp() {
        registrationService = new RepositoryUserService(passwordEncoder, repository);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndDuplicateEmail_ShouldCheckThatEmailIsUnique() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(new User());

        catchException(registrationService).registerNewUserAccount(registration);

        verify(repository, times(1)).findByEmail(REGISTRATION_EMAIL_ADDRESS);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndDuplicateEmail_ShouldThrowException() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(new User());

        catchException(registrationService).registerNewUserAccount(registration);

        assertThat(caughtException()).isExactlyInstanceOf(DuplicateEmailException.class);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndDuplicateEmail_ShouldNotSaveNewUserAccount() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(new User());

        catchException(registrationService).registerNewUserAccount(registration);

        verify(repository, never()).save(isA(User.class));
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndDuplicateEmail_ShouldNotCreateEncodedPasswordForUser() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(new User());

        catchException(registrationService).registerNewUserAccount(registration);

        verifyZeroInteractions(passwordEncoder);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndUniqueEmail_ShouldCheckThatEmailIsUnique() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);

        registrationService.registerNewUserAccount(registration);

        verify(repository, times(1)).findByEmail(REGISTRATION_EMAIL_ADDRESS);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndUniqueEmail_ShouldSaveNewUserAccountAndSetSignInProvider() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);

        registrationService.registerNewUserAccount(registration);

        ArgumentCaptor<User> userAccountArgument = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(userAccountArgument.capture());

        User createdUserAccount = userAccountArgument.getValue();

        assertThatUser(createdUserAccount)
                .hasEmail(REGISTRATION_EMAIL_ADDRESS)
                .hasFirstName(REGISTRATION_FIRST_NAME)
                .hasLastName(REGISTRATION_LAST_NAME)
                .isRegisteredUser()
                .isRegisteredByUsingSignInProvider(SOCIAL_SIGN_IN_PROVIDER);
    }


    @Test
    public void registerNewUserAccount_SocialSignInAndUniqueEmail_ShouldReturnCreatedUserAccount() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);

        when(repository.save(isA(User.class))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                return (User) arguments[0];
            }
        });

        User createdUserAccount = registrationService.registerNewUserAccount(registration);

        assertThatUser(createdUserAccount)
                .hasEmail(REGISTRATION_EMAIL_ADDRESS)
                .hasFirstName(REGISTRATION_FIRST_NAME)
                .hasLastName(REGISTRATION_LAST_NAME)
                .isRegisteredUser()
                .isRegisteredByUsingSignInProvider(SOCIAL_SIGN_IN_PROVIDER);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAnUniqueEmail_ShouldNotCreateEncodedPasswordForUser() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);

        registrationService.registerNewUserAccount(registration);

        verifyZeroInteractions(passwordEncoder);
    }
}
    These unit tests are written by following the instructions given in the previous parts of this tutorial.
        That class has a lot of unit tests. Are we sure that every one of them is really necessary?

        Or Maybe Not
        The obvious problem is that we wrote two unit tests which both verify that our service method checks that the email address given by the user is unique. We could fix this by combining these tests into a single unit test. After all, one test should convince us that our service method verifies that the email address given by user is unique before it creates a new user account.

        However, if we do this, we won’t find an answer to a much more interesting question. This question is:

        Should we really verify every interaction between the tested code and our mock objects?

        A few months I ago I ran into an article titled: Why Most Unit Testing is Waste by James Coplien. This article makes several good points but one of them was suits very well in this situation. James Coplien argued that we should ask one question about every test in our test suite:

        If this test fails, what business requirement is compromised?

        He also explains why this is such an important question:

        Most of the time, the answer is, “I don’t know.” If you don’t know the value of the test, then the test theoretically could have zero business value. The test does have a cost: maintenance, computing time, administration, and so forth. That means the test could have net negative value. That is the fourth category of tests to remove.

        Let’s find out what happens when we evaluate our unit tests by using this question.

        Popping Up the Question
        When the ask the question: “If this test fails, what business requirement is compromised?” about every unit test of our test class, we get the following answers:

        The service method checks that email address is unique when a duplicate email address is given.
        User must have a unique email address.
        The DuplicateEmailException is thrown when a duplicate email address is given.
        User must have a unique email address.
        The service method doesn’t save a new account to the database when a duplicate email address is given.
        User must have a unique email address.
        Our service method doesn’t encode the user’s password if a duplicate email address is given.
        –
        Our service method checks that the email address is unique when a unique email address is given.
        User must have a unique email address.
        When a unique email address is given, our service method creates a new User object that contains the correct information and saves the information of the created User object to the used database.
        If the registered user account has unique email address, it must be saved to the database.
        If the registered user account is created by using social sign in, our service method must save the used social sign in provider.
        When a unique email address is given, our service method returns the information of the created user account.
        Our service method must return the information of the created user account.
        When a unique email address is given and a social sign in is used, our service method must not set the password of the created user account (or encode it).
        User account that is created by using social sign in has no password.
        At first it looks like our test class has only one unit test that has no business value (or which might have a negative net value). This unit test ensures that there are no interactions between our code and the PasswordEncoder mock when a user tries to create a new user account by using a duplicate email address.

        It is clear that we must delete this unit test, but this is not the only unit test that must be deleted.

        The Rabbit Hole Is Deeper than Expected
        Earlier we noticed that our test class contains two unit tests that both verify that the findByEmail() method of the UserRepository interface is called. When we take a closer look at the implementation of the tested service method, we notice that:

        Our service method throws a DuplicateEmailException when the findByEmail() method of the UserRepository interface returns a User object.
        Our service method creates a new user account when the findByEmail() method of the UserRepository interface returns null.
        The relevant part of the tested service method looks as follows:

        1
        2
        3
        4
        5
        6
        7
        8
        9
        10
        11
        12
        13
        14
        15
        16
        17
        18
public User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException {
        if (emailExist(userAccountData.getEmail())) {
        //If the PersonRepository returns a Person object, an exception is thrown.
        throw new DuplicateEmailException("The email address: " + userAccountData.getEmail() + " is already in use.");
        }

        //If the PersonRepository returns null, the execution of this method continues.
        }

private boolean emailExist(String email) {
        User user = repository.findByEmail(email);

        if (user != null) {
        return true;
        }

        return false;
        }
        I argue that we should remove both of these unit tests because of two reasons:

        As long as we have configured the PersonRepository mock correctly, we know that its findByEmail() method was called by using the correct method parameter. Although we can link these test cases to a business requirement (user’s email address must be unique), we don’t need them to verify that this business requirement isn’t compromised.
        These unit tests don’t document the API of our service method. They document its implementation. Tests like this are harmful because they litter our test suite with irrelevant tests and they make refactoring harder.
        If we don’t configure our mock objects, they return “nice” values. The Mockito FAQ states that:
        In order to be transparent and unobtrusive all Mockito mocks by default return ‘nice’ values. For example: zeros, falseys, empty collections or nulls. Refer to javadocs about stubbing to see exactly what values are returned by default.

        This why we should always configure the relevant mock objects! If we don’t do so, our tests might be useless.

        Let’s move on and clean up this mess.

        Cleaning Up the Mess
        After we have removed these unit tests from our test class, its source code looks as follows:

        1
        2
        3
        4
        5
        6
        7
        8
        9
        10
        11
        12
        13
        14
        15
        16
        17
        18
        19
        20
        21
        22
        23
        24
        25
        26
        27
        28
        29
        30
        31
        32
        33
        34
        35
        36
        37
        38
        39
        40
        41
        42
        43
        44
        45
        46
        47
        48
        49
        50
        51
        52
        53
        54
        55
        56
        57
        58
        59
        60
        61
        62
        63
        64
        65
        66
        67
        68
        69
        70
        71
        72
        73
        74
        75
        76
        77
        78
        79
        80
        81
        82
        83
        84
        85
        86
        87
        88
        89
        90
        91
        92
        93
        94
        95
        96
        97
        98
        99
        100
        101
        102
        103
        104
        105
        106
        107
        108
        109
        110
        111
        112
        113
        114
        115
        116
        117
        118
        119
        120
        121
        122
        123
        124
        125
        126
        127
        128
        129
        130
        131
        132
        133
        134
        135
        136
        137
        138
        139
        140
        141
        142
        143
        144
        145
        import org.junit.Before;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.mockito.ArgumentCaptor;
        import org.mockito.Mock;
        import org.mockito.invocation.InvocationOnMock;
        import org.mockito.runners.MockitoJUnitRunner;
        import org.mockito.stubbing.Answer;
        import org.springframework.security.crypto.password.PasswordEncoder;

        import static com.googlecode.catchexception.CatchException.catchException;
        import static com.googlecode.catchexception.CatchException.caughtException;
        import static org.assertj.core.api.Assertions.assertThat;
        import static org.mockito.Matchers.isA;
        import static org.mockito.Mockito.never;
        import static org.mockito.Mockito.times;
        import static org.mockito.Mockito.verify;
        import static org.mockito.Mockito.verifyZeroInteractions;
        import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserServiceTest {

    private static final String REGISTRATION_EMAIL_ADDRESS = "john.smith@gmail.com";
    private static final String REGISTRATION_FIRST_NAME = "John";
    private static final String REGISTRATION_LAST_NAME = "Smith";
    private static final SocialMediaService SOCIAL_SIGN_IN_PROVIDER = SocialMediaService.TWITTER;

    private RepositoryUserService registrationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository repository;

    @Before
    public void setUp() {
        registrationService = new RepositoryUserService(passwordEncoder, repository);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndDuplicateEmail_ShouldThrowException() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(new User());

        catchException(registrationService).registerNewUserAccount(registration);

        assertThat(caughtException()).isExactlyInstanceOf(DuplicateEmailException.class);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndDuplicateEmail_ShouldNotSaveNewUserAccount() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(new User());

        catchException(registrationService).registerNewUserAccount(registration);

        verify(repository, never()).save(isA(User.class));
    }

    @Test
    public void registerNewUserAccount_SocialSignInAndUniqueEmail_ShouldSaveNewUserAccountAndSetSignInProvider() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);

        registrationService.registerNewUserAccount(registration);

        ArgumentCaptor<User> userAccountArgument = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(userAccountArgument.capture());

        User createdUserAccount = userAccountArgument.getValue();

        assertThatUser(createdUserAccount)
                .hasEmail(REGISTRATION_EMAIL_ADDRESS)
                .hasFirstName(REGISTRATION_FIRST_NAME)
                .hasLastName(REGISTRATION_LAST_NAME)
                .isRegisteredUser()
                .isRegisteredByUsingSignInProvider(SOCIAL_SIGN_IN_PROVIDER);
    }


    @Test
    public void registerNewUserAccount_SocialSignInAndUniqueEmail_ShouldReturnCreatedUserAccount() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);

        when(repository.save(isA(User.class))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                return (User) arguments[0];
            }
        });

        User createdUserAccount = registrationService.registerNewUserAccount(registration);

        assertThatUser(createdUserAccount)
                .hasEmail(REGISTRATION_EMAIL_ADDRESS)
                .hasFirstName(REGISTRATION_FIRST_NAME)
                .hasLastName(REGISTRATION_LAST_NAME)
                .isRegisteredUser()
                .isRegisteredByUsingSignInProvider(SOCIAL_SIGN_IN_PROVIDER);
    }

    @Test
    public void registerNewUserAccount_SocialSignInAnUniqueEmail_ShouldNotCreateEncodedPasswordForUser() throws DuplicateEmailException {
        RegistrationForm registration = new RegistrationFormBuilder()
                .email(REGISTRATION_EMAIL_ADDRESS)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .isSocialSignInViaSignInProvider(SOCIAL_SIGN_IN_PROVIDER)
                .build();

        when(repository.findByEmail(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);

        registrationService.registerNewUserAccount(registration);

        verifyZeroInteractions(passwordEncoder);
    }
}
    We removed three unit tests from our test class, and as a result, we can enjoy the following benefits:

        Our test class has less unit unit tests. This might seem like a strange benefit because often we are advised to write as many unit tests as possible. However, if we think about this, having less unit tests makes sense because we have less tests to maintain. This and the fact that each unit tests only one thing makes our code easier to maintain and refactor.
        We have improved the quality of our documentation. The removed unit tests didn’t document the public API of the tested service method. They documented its implementation. Because these tests were removed, it is easier to figure out the requirements of the tested service method.
        My "Test With Spring" course helps you to write unit, integration, and end-to-end tests for Spring and Spring Boot Web Apps:
        CHECK IT OUT >>

        Summary
        This blog post has taught us three things:

        If we cannot identify the business requirement that is compromised if a unit test fails, we shouldn’t write that test.
        We should not write unit tests that don’t document the public API of the tested method because these tests make our code (and tests) harder to maintain and refactor.
        If we find existing unit tests that break these two rules, we should delete them.
        We have achieved a lot during this tutorial. Do you think that it is possible to make these unit tests even better?


        ​Get Started With JUnit 5

        ​This seven part email course will help you to start writing unit tests with JUnit 5.

        Email
        SUBSCRIBE
        RELATED POSTS
        GENERAL /

        Rod Johnson Is Right: The Scala Community Need to Grow Up
        WEEKLY /

        Java Testing Weekly 20 / 2017
        WEEKLY /

        Java Testing Weekly 40 / 2017
        14 comments… add one
        edwise Link
        August 5, 2014, 14:44
        Good post!

        Only one thing, I think you have an error in line 36 of the RepositoryUserService.java, it repeat “registered” User object :)

        REPLY
        Petri Link
        August 5, 2014, 15:37
        Yes, that is indeed an error. I removed the line 36 from the RepositoryUserService class. Thank you for pointing this error out.

        Also, it is nice to hear that you enjoyed reading this blog post.

        REPLY
        Ben C Link
        August 6, 2014, 15:01
        This is a great post! I love seeing technical unit testing articles like this. Not only is it very useful from a “how do I do it” point of view, but it discusses the finer points of testing in general which is a rare thing to find it seems. Keep up the good work!

        REPLY
        Petri Link
        August 6, 2014, 20:19
        Thank you for your kind words. I really appreciate them.

        REPLY
        Magnus Lassi Link
        August 6, 2014, 16:01
        Good post Petri, I liked how you structured it.

        In principle I agree with what you said and I agree that the vast majority of tests should be related to business functionality.

        However, I think there are a few cases when you want to have a tests that aren’t directly related to a business features. One example I can think of is helper classes that converts between formats and when you want to ensure that it handles the edge cases correctly. I’ve worked on a large web application where we had a http serialization tests to ensure that what people were coding to be put in the session could be serialized and wasn’t too large. You would be surprised how many times that test helped us but perhaps that tells you more regarding the experience level of some of the contractors working on the application at the time :-)

        REPLY
        Petri Link
        August 6, 2014, 21:01
        Thank you. I am happy to hear that this blog post was useful to you.

        In principle I agree with what you said and I agree that the vast majority of tests should be related to business functionality.

        I think that unit and integration tests should be based on the requirements of the tested method or feature, but there is also a third test category which you mentioned in your comment.

        However, I think there are a few cases when you want to have a tests that aren’t directly related to a business features.

        I agree. I have actually written a blog post that describes how you can write a test which ensures that all service methods are annotated with the @Transactional annotation.

        One example I can think of is helper classes that converts between formats and when you want to ensure that it handles the edge cases correctly. I’ve worked on a large web application where we had a http serialization tests to ensure that what people were coding to be put in the session could be serialized and wasn’t too large.

        Yes. This is a perfect example of a test that is very useful, but it really isn’t a unit test or an integration test.

        You would be surprised how many times that test helped us but perhaps that tells you more regarding the experience level of some of the contractors working on the application at the time

        Actually, I can believe that this test is very useful. I have written some tests that belong to this category and they can be very useful (if you write the right tests).

        REPLY
        Jamess Link
        August 13, 2014, 15:21
        Great article, I have to agree with Magnus somewhat, I think what you’ve done is move Unit Testing away from black box style testing where each set of inputs should have a deterministic output and more towards the current trend of BDD influenced unit testing. Both have their place, when implementing code from user stories the BDD style makes TDD even more natural while for common code (libraries, utilities, whatever) the black box approach ensures more consistent behaviour and thus reuse.

        At any point when coding asking the question “what’s the purpose of this test” is always good!

        REPLY
        Petri Link
        August 16, 2014, 01:08
        I think what you’ve done is move Unit Testing away from black box style testing where each set of inputs should have a deterministic output and more towards the current trend of BDD influenced unit testing.

        I agree with your assessment. I still write black box style tests for classes that don’t have any external dependencies or have dependencies that don’t need to be mocked or stubbed (I use real objects in this case). But at the moment most unit tests that I write are following the “current trend”.

        Both have their place, when implementing code from user stories the BDD style makes TDD even more natural while for common code (libraries, utilities, whatever) the black box approach ensures more consistent behaviour and thus reuse.

        Again, I agree. At the moment I am trying to find a way to combine these approaches in my unit tests. One way to do this is to reduce the number of unit tests by using different test doubles and even “real” objects in the same unit tests. This might not be the only way to do this, but I am currently using this method, and it will be interesting to see where it will take me.

        At any point when coding asking the question “what’s the purpose of this test” is always good!

        Indeed!

        REPLY
        Charles Roth Link
        March 13, 2015, 17:53
        Good article. One minor disagreement:
        “If we cannot identify the business requirement that is compromised if a unit test fails, we shouldn’t write that test.”

        In many (not all) cases, I would change the end of the sentence to “… we should GO FIND OUT what the relevant business requirement is!” And then either (re)write the test, or delete it.

        REPLY
        Petri Link
        March 14, 2015, 22:13
        I agree that we should not write any code (tests or otherwise) if we don’t understand the business requirements. In other words, figuring out the business requirements should be our first priority. I have actually written about this in these blog posts:

        From Top to Bottom – TDD for Web Applications
        From Idea to Code: The Lifecycle of Agile Specifications
        REPLY
        Dmitry Link
        October 21, 2017, 19:45
        Thanks for article.
        But why don’t you werify that you register new user with password realy encoded with passwordEncoder?
        With this method all tests will passed well:
private String encodePassword(RegistrationForm dto) {
        String encodedPassword = null;

        if (dto.isNormalRegistration()) {
// encodedPassword = passwordEncoder.encode(dto.getPassword());
        }

        return encodedPassword;
        }

        REPLY
        Petri Link
        October 27, 2017, 22:14
        Hi,

        But why don’t you werify that you register new user with password realy encoded with passwordEncoder?

        This is a good question. I left a lot of tests out from this blog post mainly because I wanted to keep the test class as short as possible (not sure if I did a good job though).

        REPLY
        Leave a Comment

        Name
        Name (required)

        Comment

        Save my name, email, and website in this browser for the next time I comment.

        PREVIOUS POST: WRITING TESTS FOR DATA ACCESS CODE – DON’T FORGET THE DATABASE

        NEXT POST: WRITING TESTS FOR DATA ACCESS CODE – DATA MATTERS

        ​Get Started With JUnit 5

        ​This seven part email course helps you to start writing unit tests with JUnit 5.

        Email...
        Register Now
        WRITING CLEAN TESTS

        Three Reasons Why We Should Not Use Inheritance In Our Tests
        It Starts From the Configuration
        Naming Matters
        Beware of Magic
        New Considered Harmful
        Replace Assertions with a Domain-Specific Language
        Divide and Conquer
        To Verify Or Not To Verify
        Trouble in Paradise
        Small Is Beautiful
        Java 8 to the Rescue
        WRITE BETTER TESTS

        Test With Spring Course
        Java Testing Weekly
        JUnit 5 Tutorial
        Spring MVC Test Tutorial
        TestProject Tutorial
        WireMock Tutorial
        Writing Clean Tests
        Writing Tests for Data Access Code
        MASTER SPRING FRAMEWORK

        Spring Data JPA Tutorial
        Spring Data Solr Tutorial
        Spring From the Trenches
        Spring MVC Test Tutorial
        Spring Social Tutorial
        Using jOOQ with Spring
        BUILD YOUR APPLICATION

        Getting Started With Gradle
        Maven Tutorial
        FIND THE BEST TUTORIALS

        JUnit 5 - The Ultimate Resource
        Spring Batch - The Ultimate Resource
        SEARCH

        enter search term and press enter
        FROM THE BLOG

        RecentPopularFavorites
        Java Testing Weekly 15 / 2019
        Java Testing Weekly 14 / 2019
        Java Testing Weekly 13 / 2019
        Java Testing Weekly 12 / 2019
        Java Testing Weekly 11 / 2019
        HOW TO WRITE BETTER TESTS?

        If you are struggling to write automated tests that embrace change, you should find out how my testing course can help you to write tests for Spring and Spring Boot applications.
        © 2010-Present Petri Kainulainen (all code samples are licensed under Apache License 2.0)
        Sitemap | Cookie Policy | Privacy Policy

        Due to GDPR, we have published our new privacy policy. CloseRead Privacy Policy
