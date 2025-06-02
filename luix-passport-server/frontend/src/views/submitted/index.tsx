import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"

export default function ContactSuccess() {
  return (
    <div className="container relative grid flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
      {/* Left side - matches contact form layout */}
      <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
            Successfully Sent!
          </h2>
          <h4 className="mt-6 text-lg">
            We've received your message
          </h4>
          <p className="text-sm mt-10">
            Our team is already reviewing your inquiry
          </p>
        </div>
      </div>

      {/* Right side - success confirmation */}
      <div className="flex flex-col w-full xl:px-48 lg:px-28 px-5 rounded-3xl py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />

        <div className="w-full max-w-md mx-auto text-center">
          {/* Main message */}
          <h1 className="text-3xl font-bold mb-3">Thank You!</h1>
          <p className="text-lg text-muted-foreground mb-2">
            Your contact request has been submitted.
          </p>
          <p className="text-sm text-muted-foreground mb-8">
            We'll respond to you within 24 hours.
          </p>

          {/* Action buttons */}
          <div className="flex flex-col space-y-3 mt-8">
            <Button asChild className="w-full h-14 rounded-2xl">
              <Link to="/">
                Back to Homepage
              </Link>
            </Button>

            <Button
              variant="outline"
              asChild
              className="w-full h-14 rounded-2xl"
            >
              <Link to="/contact-us">
                Send Another Message
              </Link>
            </Button>
          </div>

          {/* Immediate help section */}
          <div className="mt-12 pt-8">
            <h3 className="text-lg font-semibold mb-4">Need immediate assistance?</h3>
            <div className="space-y-3 text-sm">
              <div className="flex items-center justify-center">
                <svg className="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                <span>+1 (555) 123-4567</span>
              </div>
              <div className="flex items-center justify-center">
                <svg className="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <span>support@luixtech.cn</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}