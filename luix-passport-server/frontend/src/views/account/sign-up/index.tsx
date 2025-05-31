import { SignUpForm } from "./sign-up-form";

export default function SignUp() {
  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2 bg-white">
      {/* Left side - hidden on mobile */}
      <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
            Join Our Community
          </h2>
          <h4 className="mt-6 text-lg">
            Create your account to unlock exclusive features and personalized experiences.
            Get started in just a few clicks.
          </h4>
          <p className="text-sm mt-10">
            Already have an account?{" "}
            <a href="/login" className="text-blue-600 font-semibold hover:underline">
              Sign in here
            </a>
          </p>
        </div>
      </div>

      {/* Right side - form */}
      <div className="flex flex-col w-full h-full xl:px-48 lg:px-28 px-5 text-center bg-white rounded-3xl">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />
        <SignUpForm />
        <p className="mt-5 px-8 text-center text-sm text-muted-foreground">
          By creating an account, you agree to our{" "}
          <a href="/terms" className="underline hover:text-primary">
            Terms of Service
          </a>{" "}
          and{" "}
          <a href="/privacy" className="underline hover:text-primary">
            Privacy Policy
          </a>
          .
        </p>
      </div>
    </div>
  );
}