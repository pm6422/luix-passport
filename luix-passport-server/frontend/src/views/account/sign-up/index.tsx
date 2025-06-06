import { SignUpForm } from "./sign-up-form";
import { Link } from "react-router-dom"

export default function SignUp() {
  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
      {/* Left side - hidden on mobile */}
      <div className="lg:flex relative hidden xl:px-28 lg:px-10 ms-10 mb-36">
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
            <Link to="/sign-in" className="font-bold text-primary hover:underline">Sign in here</Link>
          </p>
        </div>
      </div>

      {/* Right side - form */}
      <div className="flex flex-col w-full xl:px-48 lg:px-10 px-5 text-center rounded-3xl">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />
        <SignUpForm />
        <p className="mt-5 px-8 text-center text-sm text-muted-foreground">
          By creating an account, you agree to our{" "}
          <Link to="/terms-of-service" className="underline hover:text-primary">
            Terms of Service
          </Link>{" "}
          and{" "}
          <Link to="/privacy-policy" className="underline hover:text-primary">
            Privacy Policy
          </Link>
        </p>
      </div>
    </div>
  );
}