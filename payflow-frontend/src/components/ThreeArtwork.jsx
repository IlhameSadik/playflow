import { useEffect, useRef } from "react";
import * as THREE from "three";

export default function ThreeArtwork() {
  const containerRef = useRef(null);

  useEffect(() => {
    const SIZE = 190;
    const scene = new THREE.Scene();

    const camera = new THREE.PerspectiveCamera(40, 1, 0.1, 100);
    camera.position.z = 4;

    const renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    renderer.setSize(SIZE, SIZE);
    renderer.outputColorSpace = THREE.SRGBColorSpace;
    containerRef.current.appendChild(renderer.domElement);

    const character = new THREE.Group();
    scene.add(character);

    // ---------- Corps : blob bleu rond et doux ----------
    const geometry = new THREE.IcosahedronGeometry(1, 64);
    const material = new THREE.MeshPhysicalMaterial({
      color: "#6BA8FF",
      roughness: 0.35,
      metalness: 0,
      clearcoat: 1,
      clearcoatRoughness: 0.25,
      sheen: 1,
      sheenColor: new THREE.Color("#BBD8FF"),
    });
    const body = new THREE.Mesh(geometry, material);
    body.scale.set(1.08, 0.95, 1);
    character.add(body);

    // ---------- Visage kawaii ----------
    const face = new THREE.Group();
    face.position.set(0, -0.02, 0.9);
    character.add(face);

    const eyeMat = new THREE.MeshPhysicalMaterial({
      color: "#0F1B4D",
      roughness: 0.1,
      clearcoat: 1,
    });
    const shineMat = new THREE.MeshBasicMaterial({ color: "#ffffff" });

    const eyes = [];
    [-0.38, 0.38].forEach((x) => {
      const eye = new THREE.Group();
      eye.position.set(x, 0.08, 0);

      const ball = new THREE.Mesh(new THREE.SphereGeometry(0.17, 32, 32), eyeMat);
      ball.scale.set(1, 1.25, 0.5);
      eye.add(ball);

      const big = new THREE.Mesh(new THREE.SphereGeometry(0.065, 20, 20), shineMat);
      big.position.set(0.05, 0.1, 0.1);
      eye.add(big);

      const small = new THREE.Mesh(new THREE.SphereGeometry(0.03, 16, 16), shineMat);
      small.position.set(-0.05, -0.08, 0.1);
      eye.add(small);

      face.add(eye);
      eyes.push(eye);
    });

    // Grosses joues roses
    const blushMat = new THREE.MeshBasicMaterial({
      color: "#FF8FB1",
      transparent: true,
      opacity: 0.6,
    });
    [-0.66, 0.66].forEach((x) => {
      const blush = new THREE.Mesh(new THREE.CircleGeometry(0.14, 32), blushMat);
      blush.position.set(x, -0.12, -0.05);
      blush.scale.set(1.4, 0.85, 1);
      face.add(blush);
    });

    // Petite bouche en "w"
    const mouthMat = new THREE.MeshBasicMaterial({ color: "#0F1B4D" });
    [-0.045, 0.045].forEach((x) => {
      const curve = new THREE.QuadraticBezierCurve3(
        new THREE.Vector3(x - 0.045, -0.1, 0),
        new THREE.Vector3(x, -0.17, 0.02),
        new THREE.Vector3(x + 0.045, -0.1, 0)
      );
      face.add(new THREE.Mesh(new THREE.TubeGeometry(curve, 16, 0.011, 8, false), mouthMat));
    });

    // ---------- Lumières ----------
    scene.add(new THREE.AmbientLight(0xffffff, 1.6));
    const light = new THREE.DirectionalLight(0xffffff, 2.8);
    light.position.set(3, 4, 5);
    scene.add(light);

    const originalPositions = geometry.attributes.position.array.slice();
    let animationId;
    const clock = new THREE.Clock();

    // ---------- Suivi de la souris ----------
    const mouse = { x: 0, y: 0 }; // cible (-1 à 1)
    const look = { x: 0, y: 0 }; // valeur lissée

    const onMove = (e) => {
      const rect = containerRef.current?.getBoundingClientRect();
      if (!rect) return;
      const cx = rect.left + rect.width / 2;
      const cy = rect.top + rect.height / 2;
      mouse.x = THREE.MathUtils.clamp((e.clientX - cx) / 300, -1, 1);
      mouse.y = THREE.MathUtils.clamp((e.clientY - cy) / 300, -1, 1);
    };
    const onLeave = () => {
      mouse.x = 0;
      mouse.y = 0;
    };
    window.addEventListener("pointermove", onMove);
    document.addEventListener("pointerleave", onLeave);

    const animate = () => {
      animationId = requestAnimationFrame(animate);
      const time = clock.getElapsedTime();
      const positions = geometry.attributes.position;

      for (let i = 0; i < positions.count; i++) {
        const x = originalPositions[i * 3];
        const y = originalPositions[i * 3 + 1];
        const z = originalPositions[i * 3 + 2];
        const wave =
          1 + 0.03 * Math.sin(y * 3 + time * 1.5) + 0.02 * Math.cos(x * 4 + time);
        positions.setXYZ(i, x * wave, y * wave, z * wave);
      }
      positions.needsUpdate = true;
      geometry.computeVertexNormals();

      // Petit rebond "squash & stretch"
      const bounce = Math.sin(time * 2.2);
      character.position.y = Math.abs(bounce) * 0.06;
      const squash = 1 - Math.abs(bounce) * 0.04;
      character.scale.set(1 / Math.sqrt(squash), squash, 1 / Math.sqrt(squash));
      // Le corps se tourne doucement vers la souris
      look.x += (mouse.x - look.x) * 0.08;
      look.y += (mouse.y - look.y) * 0.08;
      character.rotation.y = look.x * 0.6;
      character.rotation.x = look.y * 0.4;
      character.rotation.z = Math.sin(time * 1.1) * 0.03;

      // Le visage glisse un peu vers la souris
      face.position.x = look.x * 0.12;
      face.position.y = -0.02 - look.y * 0.08;

      // Clignement des yeux
      const blink = time % 3.6 > 3.5 ? 0.1 : 1;
      eyes.forEach((e) => (e.scale.y = blink));

      renderer.render(scene, camera);
    };
    animate();

    const mount = containerRef.current;
    return () => {
      cancelAnimationFrame(animationId);
      window.removeEventListener("pointermove", onMove);
      document.removeEventListener("pointerleave", onLeave);
      geometry.dispose();
      material.dispose();
      renderer.dispose();
      mount?.removeChild(renderer.domElement);
    };
  }, []);

  // Seule la moitié haute est visible : le bas est coupé par le bord de la carte
  return (
    <div
      style={{
        width: "190px",
        height: "97px",
        margin: "0 auto",
        overflow: "hidden",
        position: "relative",
        pointerEvents: "none",
      }}
    >
      <div
        ref={containerRef}
        style={{
          width: "190px",
          height: "190px",
          position: "absolute",
          top: "-25px",
          left: 0,
        }}
      />
    </div>
  );
}