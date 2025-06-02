import { useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { AccountService } from "@/services/account-service";
import { Button } from "@/components/custom/button";
import { toast } from "sonner";
import { Link } from "react-router-dom";
import { getErrorMessage } from "@/lib/handle-error";

export default function ActivateAccount() {
  const [searchParams] = useSearchParams();
  const code = searchParams.get("code");
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!code) {
      toast.error("Invalid empty activation code");
    } else {
      toast.promise(AccountService.activate(code), {
        loading: "Activating account...",
        success: () => {
          setSuccess(true);
          return "Activated account successfully";
        },
        error: (error) => {
          setSuccess(false);
          return getErrorMessage(error);
        }
      });
    }
  }, [code]);

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
            Account Activation
          </h2>
          <h4 className="mt-6 text-lg">
            Complete your registration and unlock full access to our platform
          </h4>
          <p className="text-sm mt-10">
            Need help?{" "}
            <Link to="/contact-us" className="font-bold text-primary hover:underline">
              Contact support
            </Link>
          </p>
        </div>
      </div>

      {/* Right side - content */}
      <div className="flex flex-col w-full xl:px-48 lg:px-28 px-5 rounded-3xl py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />

        <div className="text-center">
          <h1 className="text-3xl font-bold mb-2">Activate Account</h1>

          <div className="mt-6 mb-8">
            {success ? (
              <div className="space-y-4">
                <p className="text-lg text-muted-foreground">
                  Your account has been successfully activated!
                </p>
                <Button className="w-full py-4 h-14 rounded-2xl">
                  <a href="/login">
                    Continue to Login
                  </a>
                </Button>
              </div>
            ) : (
              <div className="space-y-4">
                <p className="text-lg text-muted-foreground">
                  {code ? "Activating your account..." : "Invalid activation link"}
                </p>
                {!code && (
                  <Button variant="outline" className="w-full py-4 h-14 rounded-2xl">
                    <Link to="/sign-up">Sign Up</Link>
                  </Button>
                )}
              </div>
            )}
          </div>

          <p className="mt-8 px-8 text-center text-sm text-muted-foreground">
            By activating your account, you agree to our{" "}
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
    </div>
  );
}