import { Link } from "react-router-dom"

export default function TermsOfService() {
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
            Our Terms of Service
          </h2>
          <h4 className="mt-6 text-lg">
            Understanding these terms helps ensure a smooth experience for everyone.
          </h4>
          <p className="text-sm mt-10">
            Need help?{" "}
            <Link to="/contact-us" className="font-bold text-blue-600 hover:underline">
              Contact us
            </Link>
          </p>
        </div>
      </div>

      {/* Right side - content */}
      <div className="flex flex-col w-full h-full xl:px-48 lg:px-28 px-5 text-left bg-white rounded-3xl overflow-y-auto py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />

        <div className="prose max-w-none">
          <h1 className="text-2xl font-bold mb-6 text-center">Terms of Service</h1>
          <p className="text-sm text-muted-foreground mb-8 text-center">Last Updated: January 1, 2023</p>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">1. Acceptance of Terms</h2>
            <p className="mb-4">
              By accessing or using our services, you agree to be bound by these Terms of Service.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">2. User Responsibilities</h2>
            <p className="mb-4">
              You are responsible for maintaining the confidentiality of your account and password.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">3. Prohibited Activities</h2>
            <ul className="list-disc pl-5 mb-4">
              <li className="mb-2">Violating laws or regulations</li>
              <li className="mb-2">Infringing intellectual property rights</li>
              <li className="mb-2">Distributing malware or harmful code</li>
            </ul>
          </section>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">4. Termination</h2>
            <p className="mb-4">
              We may terminate or suspend access to our services immediately, without prior notice.
            </p>
          </section>

          <p className="text-sm text-muted-foreground mt-10">
            By using our services, you acknowledge that you have read and understood these Terms.
          </p>
        </div>
      </div>
    </div>
  );
}