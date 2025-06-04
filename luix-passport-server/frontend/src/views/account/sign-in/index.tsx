import { SignInForm } from "./sign-in-form";
import { Link } from "react-router-dom"

export default function SignIn() {
  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
      {/* Left side - hidden on mobile */}
      <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
            Seamless Login for Exclusive Access
          </h2>
          <h4 className="mt-6 text-lg">
            Immerse yourself in a hassle-free login journey with our account protection technology. Effortlessly access your account.
          </h4>
          <p className="text-sm mt-10">
            Back to {" "}
            <Link to="/" className="font-bold text-primary hover:underline">
              Home
            </Link>
          </p>
        </div>
      </div>

      {/* Right side - form */}
      <div className="flex flex-col w-full xl:px-48 lg:px-28 px-5 rounded-3xl py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />
        <h3 className="mb-6 text-2xl font-extrabold text-center">Sign In</h3>

        <SignInForm />

        <p className="mt-6 text-sm text-center text-muted-foreground">
          Not registered yet?{" "}
          <Link to="/sign-up" className="font-bold text-primary hover:underline">
            Create an Account
          </Link>
        </p>

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